package com.lalitVerma.cloudShare.controller;

import com.lalitVerma.cloudShare.dto.FileMetaDataDTO;
import com.lalitVerma.cloudShare.entities.FileMetaData;
import com.lalitVerma.cloudShare.entities.UserCredits;
import com.lalitVerma.cloudShare.services.FileMetaDataService;
import com.lalitVerma.cloudShare.services.UserCreditsService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final FileMetaDataService fileMetaDataService;
    private final UserCreditsService  userCreditsService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(@RequestPart("files")MultipartFile[] files) throws IOException {
        Map<String, Object> map = new HashMap<>();
        List<FileMetaDataDTO> list = this.fileMetaDataService.uploadFiles(files);

        UserCredits finalCredits = this.userCreditsService.getUserCredits();

        map.put("files", list);
        map.put("remainingCredits",finalCredits.getCredits());

        return ResponseEntity.ok(map);
    }

    @GetMapping("/my-files")
    public ResponseEntity<?> getFilesForCurrentUser() {
        List<FileMetaDataDTO> files = this.fileMetaDataService.getAllFiles();

        return ResponseEntity.ok(files);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<?> getPublicFil(@PathVariable String id) {
        FileMetaDataDTO fileMetaData = this.fileMetaDataService.getPublicFile(id);
        return ResponseEntity.ok(fileMetaData);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String id) throws FileNotFoundException, IOException {
        FileMetaDataDTO downloadableFile = fileMetaDataService.getDownloadableFile(id);

        Path path = Paths.get(downloadableFile.getFileLocation());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+downloadableFile.getName()+"\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable String id) throws IOException {
        this.fileMetaDataService.deleteFile(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    @PatchMapping("/toggle-public/{id}")
    public ResponseEntity<?> togglePublicAccess(@PathVariable String id) throws FileNotFoundException {
        FileMetaDataDTO file = this.fileMetaDataService.togglePublicAccess(id);

        return ResponseEntity.ok(file);
    }
}
