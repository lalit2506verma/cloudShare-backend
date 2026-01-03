package com.lalitVerma.cloudShare.repository;

import com.lalitVerma.cloudShare.entities.FileMetaData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FileMetaDataRepository extends MongoRepository<FileMetaData, String> {

    List<FileMetaData> findByUserId(String userId);

    Long countByUserId(String userId);
}
