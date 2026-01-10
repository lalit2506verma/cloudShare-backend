package com.lalitVerma.cloudShare.services;

import com.lalitVerma.cloudShare.dto.PaymentDTO;
import com.lalitVerma.cloudShare.dto.PaymentVerificationDTO;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService {

    PaymentDTO createOrder(PaymentDTO paymentDTO);

    PaymentDTO verifyPayment(PaymentVerificationDTO paymentVerificationDTO);
}
