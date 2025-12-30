package com.lalitVerma.cloudShare.repository;

import com.lalitVerma.cloudShare.entities.UserCredits;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserCreditsRepository extends MongoRepository<UserCredits, String> {
}
