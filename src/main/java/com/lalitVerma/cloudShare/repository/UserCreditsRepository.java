package com.lalitVerma.cloudShare.repository;

import com.lalitVerma.cloudShare.entities.UserCredits;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserCreditsRepository extends MongoRepository<UserCredits, String> {

    Optional<UserCredits> findByUserId(String userId);
}
