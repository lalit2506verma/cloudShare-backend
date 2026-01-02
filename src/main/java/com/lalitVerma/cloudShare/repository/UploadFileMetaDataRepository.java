package com.lalitVerma.cloudShare.repository;

import com.lalitVerma.cloudShare.entities.UploadFileMetaData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UploadFileMetaDataRepository extends MongoRepository<UploadFileMetaData, String> {

    List<UploadFileMetaData> findByUserId(String userId);

    Long countByUserId(String userId);
}
