package com.lalitVerma.cloudShare.repository;

import com.lalitVerma.cloudShare.entities.PaymentTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PaymentTransactionRepository extends MongoRepository<PaymentTransaction, String> {

    List<PaymentTransaction> findByUserId(String userId);

    List<PaymentTransaction> findByUserIdOrderByTransactionDate(String userId);

    List<PaymentTransaction> findByUserIdAndStatusOrderByTransactionDateDesc(String userId, String status);

    PaymentTransaction findByOrderId(String orderId);
}
