package com.lalitVerma.cloudShare.controller;

import com.lalitVerma.cloudShare.dto.UserCreditsDTO;
import com.lalitVerma.cloudShare.entities.UserCredits;
import com.lalitVerma.cloudShare.services.UserCreditsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserCreditsController {

    private final UserCreditsService userCreditsService;

    @GetMapping("/credits")
    public ResponseEntity<?> getUserCredits() {
        UserCredits userCredits =  this.userCreditsService.getUserCredits();
        UserCreditsDTO creditsDTO = UserCreditsDTO.builder()
                .credits(userCredits.getCredits())
                .plan(userCredits.getSubscriptionPlan())
                .build();

        return ResponseEntity.ok(creditsDTO);
    }
}
