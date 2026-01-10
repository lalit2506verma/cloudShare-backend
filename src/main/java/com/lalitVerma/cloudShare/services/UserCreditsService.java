package com.lalitVerma.cloudShare.services;

import com.lalitVerma.cloudShare.entities.UserCredits;
import org.springframework.stereotype.Service;

@Service
public interface UserCreditsService {

    UserCredits createInitialCredits(String userId);

    UserCredits addCredits(String userId, Integer creditsToAdd, String subscriptionPlan);

    UserCredits getUserCredits(String userId);

    UserCredits getUserCredits();

    boolean hasEnoughCredits(int requiredCredits);

    UserCredits consumeCredit();
}
