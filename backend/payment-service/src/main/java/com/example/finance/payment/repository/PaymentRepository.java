package com.example.finance.payment.repository;

import com.example.finance.payment.entity.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
  List<Payment> findBySourceAccountId(Long sourceAccountId);

  Optional<Payment> findByPaymentReference(String paymentReference);
}
