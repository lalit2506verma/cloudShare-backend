package com.lalitVerma.cloudShare.services;

import com.lalitVerma.cloudShare.dto.UploadFileMetaDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface UploadFileMetaDataService {

    List<UploadFileMetaDataDto> uploadFiles(MultipartFile[] files) throws IOException;

    List<UploadFileMetaDataDto> getAllFiles();
}
