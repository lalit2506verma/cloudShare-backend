package com.lalitVerma.cloudShare.services.impl;

import com.lalitVerma.cloudShare.dto.UploadFileMetaDataDto;
import com.lalitVerma.cloudShare.entities.UploadFileMetaData;
import com.lalitVerma.cloudShare.entities.User;
import com.lalitVerma.cloudShare.exception.NotEnoughCreditsException;
import com.lalitVerma.cloudShare.repository.UploadFileMetaDataRepository;
import com.lalitVerma.cloudShare.services.UploadFileMetaDataService;
import com.lalitVerma.cloudShare.services.UserCreditsService;
import com.lalitVerma.cloudShare.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UploadFileMetaDataServiceImpl implements UploadFileMetaDataService {

    private final UserService userService;
    private final UserCreditsService userCreditsService;
    private final UploadFileMetaDataRepository uploadFileMetaDataRepository;

    @Override
    public List<UploadFileMetaDataDto> uploadFiles(MultipartFile[] files) throws IOException {
        User currentUser = this.userService.getCurrentUser();

        List<UploadFileMetaData> savedFiles = new ArrayList<>();

        if (!userCreditsService.hasEnoughCredits(files.length)) {
            throw new NotEnoughCreditsException("Not enough credits to upload files");
        }

        Path uploadPath = Paths.get("upload").toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        for(MultipartFile file : files) {
            String fileName = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(file.getOriginalFilename());
            Path targetLocation = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            UploadFileMetaData fileMetaData = UploadFileMetaData.builder()
                    .fileLocation(targetLocation.toString())
                    .name(file.getOriginalFilename())
                    .size(file.getSize())
                    .type(file.getContentType())
                    .userId(currentUser.getId())
                    .isPublic(false)
                    .uploadedAt(LocalDateTime.now())
                    .build();

            this.userCreditsService.consumeCredit();

            savedFiles.add(uploadFileMetaDataRepository.save(fileMetaData));
        }

        return savedFiles.stream().map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UploadFileMetaDataDto> getAllFiles() {
        User user = this.userService.getCurrentUser();
        List<UploadFileMetaData> files = this.uploadFileMetaDataRepository.findByUserId(user.getId());
        return files.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private UploadFileMetaDataDto mapToDTO(UploadFileMetaData fileMetaData) {
        return UploadFileMetaDataDto.builder()
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
