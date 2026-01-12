package com.lalitVerma.cloudShare.controller;

import com.lalitVerma.cloudShare.entities.PaymentTransaction;
import com.lalitVerma.cloudShare.entities.User;
import com.lalitVerma.cloudShare.repository.PaymentTransactionRepository;
import com.lalitVerma.cloudShare.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TransactionController {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final UserService userService;


    @GetMapping("/transactions")
    public ResponseEntity<?> getUserTransactions(){
        User currentUser = userService.getCurrentUser();
        String userId =  currentUser.getId();

        List<PaymentTransaction> transactions = this.paymentTransactionRepository.findByUserIdAndStatusOrderByTransactionDateDesc(userId, "SUCCESS");
        return ResponseEntity.ok(transactions);
    }
}
