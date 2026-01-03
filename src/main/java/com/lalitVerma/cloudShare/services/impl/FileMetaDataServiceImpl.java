package com.lalitVerma.cloudShare.services.impl;

import com.lalitVerma.cloudShare.dto.FileMetaDataDTO;
import com.lalitVerma.cloudShare.entities.FileMetaData;
import com.lalitVerma.cloudShare.entities.User;
import com.lalitVerma.cloudShare.exception.NotEnoughCreditsException;
import com.lalitVerma.cloudShare.exception.PrivateFileAccessException;
import com.lalitVerma.cloudShare.repository.FileMetaDataRepository;
import com.lalitVerma.cloudShare.services.FileMetaDataService;
import com.lalitVerma.cloudShare.services.UserCreditsService;
import com.lalitVerma.cloudShare.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FileMetaDataServiceImpl implements FileMetaDataService {

    private final UserService userService;
    private final UserCreditsService userCreditsService;
    private final FileMetaDataRepository fileMetaDataRepository;

    @Override
    public List<FileMetaDataDTO> uploadFiles(MultipartFile[] files) throws IOException {
        User currentUser = this.userService.getCurrentUser();

        List<FileMetaData> savedFiles = new ArrayList<>();

        if (!userCreditsService.hasEnoughCredits(files.length)) {
            throw new NotEnoughCreditsException("Not enough credits to upload files");
        }

        Path uploadPath = Paths.get("upload").toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        for(MultipartFile file : files) {
            String fileName = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(file.getOriginalFilename());
            Path targetLocation = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            FileMetaData fileMetaData = FileMetaData.builder()
                    .fileLocation(targetLocation.toString())
                    .name(file.getOriginalFilename())
                    .size(file.getSize())
                    .type(file.getContentType())
                    .userId(currentUser.getId())
                    .isPublic(false)
                    .uploadedAt(LocalDateTime.now())
                    .build();

            this.userCreditsService.consumeCredit();

            savedFiles.add(fileMetaDataRepository.save(fileMetaData));
        }

        return savedFiles.stream().map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FileMetaDataDTO> getAllFiles() {
        User user = this.userService.getCurrentUser();
        List<FileMetaData> files = this.fileMetaDataRepository.findByUserId(user.getId());
        return files.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public FileMetaDataDTO getPublicFile(String id) {
        Optional<FileMetaData> fileOptional =
                this.fileMetaDataRepository.findById(id);

        // Throw Exception for accessing private files
        if(fileOptional.isEmpty() || !fileOptional.get().isPublic()) {
            throw new PrivateFileAccessException("Can't access Private Files");
        }

        // Return if file is public
        FileMetaData fileMetaData = fileOptional.get();

        return mapToDTO(fileMetaData);
    }

    @Override
    public FileMetaDataDTO getDownloadableFile(String id) throws FileNotFoundException {
        FileMetaData file = this.fileMetaDataRepository.findById(id).orElseThrow(() -> new FileNotFoundException("File not found in DB with ID: " + id));
        return mapToDTO(file);
    }

    @Override
    public void deleteFile(String fileId) throws IOException {
        // Delete only those file which user own
        User currentUser = userService.getCurrentUser();

        FileMetaData fileMetaData = fileMetaDataRepository.findById(fileId)
                .orElseThrow(() ->
                        new FileNotFoundException("File not found with id: " + fileId)
                );

        if (!fileMetaData.getUserId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can delete only your own files");
        }

        Path filePath = Paths.get(fileMetaData.getFileLocation());
        Files.deleteIfExists(filePath);

        fileMetaDataRepository.delete(fileMetaData);
    }

    @Override
    public FileMetaDataDTO togglePublicAccess(String id) throws FileNotFoundException {
        FileMetaData fileData = this.fileMetaDataRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("File not found in DB with ID: " + id));

        fileData.setPublic(!fileData.isPublic());

        return mapToDTO(this.fileMetaDataRepository.save(fileData));
    }

    private FileMetaDataDTO mapToDTO(FileMetaData fileMetaData) {
        return FileMetaDataDTO.builder()
                .id(fileMetaData.getId())
                .name(fileMetaData.getName())
                .size(fileMetaData.getSize())
                .type(fileMetaData.getType())
                .userId(fileMetaData.getUserId())
                .fileLocation(fileMetaData.getFileLocation())
                .isPublic(fileMetaData.isPublic())
                .uploadedAt(fileMetaData.getUploadedAt())
                .build();
    }
}
