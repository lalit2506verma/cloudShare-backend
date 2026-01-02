package com.lalitVerma.cloudShare.controller;

import com.lalitVerma.cloudShare.dto.UploadFileMetaDataDto;
import com.lalitVerma.cloudShare.entities.UserCredits;
import com.lalitVerma.cloudShare.services.UploadFileMetaDataService;
import com.lalitVerma.cloudShare.services.UserCreditsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final UploadFileMetaDataService uploadFileMetaDataService;
    private final UserCreditsService  userCreditsService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(@RequestPart("files")MultipartFile[] files) throws IOException {
        Map<String, Object> map = new HashMap<>();
        List<UploadFileMetaDataDto> list = this.uploadFileMetaDataService.uploadFiles(files);

        UserCredits finalCredits = this.userCreditsService.getUserCredits();

        map.put("files", list);
        map.put("remaining Credits",finalCredits.getCredits());

        return ResponseEntity.ok(map);
    }

    @GetMapping("/my-files")
    public ResponseEntity<?> getFilesForCurrentUser() {
        List<UploadFileMetaDataDto> files = this.uploadFileMetaDataService.getAllFiles();

        return ResponseEntity.ok(files);
    }
}
