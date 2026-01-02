package com.lalitVerma.cloudShare.services.impl;

import com.lalitVerma.cloudShare.entities.UserCredits;
import com.lalitVerma.cloudShare.repository.UserCreditsRepository;
import com.lalitVerma.cloudShare.services.UserCreditsService;
import com.lalitVerma.cloudShare.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCreditsServiceImpl implements UserCreditsService {

    private final UserCreditsRepository userCreditsRepository;
    private final UserService userService;

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
    public UserCredits getUserCredits(String userId) {
        return userCreditsRepository.findByUserId(userId)
                .orElseGet(() -> this.createInitialCredits(userId));
    }

    @Override
    public UserCredits getUserCredits() {
        String userId = this.userService.getCurrentUser().getId();
        return getUserCredits(userId);
    }

    @Override
    public boolean hasEnoughCredits(int requiredCredits) {
        UserCredits userCredits = getUserCredits();

        return  userCredits.getCredits() >= requiredCredits;
    }

    @Override
    public UserCredits consumeCredit() {
        UserCredits userCredits = getUserCredits();

        if(userCredits.getCredits() <= 0){
            return null;
        }

        userCredits.setCredits(userCredits.getCredits() - 1);
        return this.userCreditsRepository.save(userCredits);
    }

}
