package com.lalitVerma.cloudShare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadFileMetaDataDto {

    private String id;
    private String name;
    private String type;
    private Long size;
    private String userId;
    private boolean isPublic;
    private String fileLocation;
    private LocalDateTime uploadedAt;
}
