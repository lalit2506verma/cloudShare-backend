package com.lalitVerma.cloudShare.controller;

import com.lalitVerma.cloudShare.dto.PaymentDTO;
import com.lalitVerma.cloudShare.dto.PaymentVerificationDTO;
import com.lalitVerma.cloudShare.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/place-order")
    public ResponseEntity<?> createOrder(@RequestBody PaymentDTO paymentDTO){
        PaymentDTO response = this.paymentService.createOrder(paymentDTO);

        if(response.isSuccess()){
            return ResponseEntity.ok(response);
        }
        else{
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentVerificationDTO verificationDTO){
        PaymentDTO paymentDTO = this.paymentService.verifyPayment(verificationDTO);

        if(paymentDTO.isSuccess()){
            return  ResponseEntity.ok(paymentDTO);
        }
        else{
            return ResponseEntity.badRequest().body(paymentDTO);
        }
    }
}
