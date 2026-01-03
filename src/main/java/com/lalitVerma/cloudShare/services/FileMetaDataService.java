package com.lalitVerma.cloudShare.services;

import com.lalitVerma.cloudShare.dto.FileMetaDataDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
public interface FileMetaDataService {

    List<FileMetaDataDTO> uploadFiles(MultipartFile[] files) throws IOException;

    List<FileMetaDataDTO> getAllFiles();

    FileMetaDataDTO getPublicFile(String id);

    FileMetaDataDTO getDownloadableFile(String id) throws FileNotFoundException;

    void deleteFile(String id) throws IOException;

    FileMetaDataDTO togglePublicAccess(String id) throws FileNotFoundException;

}
