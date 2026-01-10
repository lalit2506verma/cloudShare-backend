package com.lalitVerma.cloudShare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDTO {

    private String planId;
    private String customerId;
    private Integer amount;
    private boolean success;
    private String orderId;
    private String message;
    private String currency;
    private Integer credits;
}
