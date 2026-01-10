package com.lalitVerma.cloudShare.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "payment_transactions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentTransaction {

    private String id;
    private String userId;
    private String orderId;
    private String paymentId;
    private String planId;
    private Integer amount;
    private Integer creditsAdded;
    private  String currency;
    private String status;
    private LocalDateTime transactionDate;
    private String userEmail;
    private String paymentMode;

}
