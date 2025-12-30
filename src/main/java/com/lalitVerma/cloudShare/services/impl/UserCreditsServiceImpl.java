package com.lalitVerma.cloudShare.services.impl;

import com.lalitVerma.cloudShare.entities.UserCredits;
import com.lalitVerma.cloudShare.repository.UserCreditsRepository;
import com.lalitVerma.cloudShare.services.UserCreditsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCreditsServiceImpl implements UserCreditsService {

    private final UserCreditsRepository userCreditsRepository;

    @Override
    public UserCredits createInitialCredits(String userId) {
        UserCredits userCredits = UserCredits
                .builder()
                .credits(5)
                .userId(userId)
                .subscriptionPlan("BASIC")
                .build();

        return this.userCreditsRepository.save(userCredits);
    }

    @Override
    public UserCredits getUserCredits(String email) {
        return null;
    }
}
