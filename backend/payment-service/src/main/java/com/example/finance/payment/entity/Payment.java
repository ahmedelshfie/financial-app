package com.example.finance.payment.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.*;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "payment_reference", nullable = false, unique = true, length = 50)
  private String paymentReference;

  @Column(name = "source_account_id", nullable = false)
  private Long sourceAccountId;

  @Column(name = "beneficiary_id", nullable = false)
  private Long beneficiaryId;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal amount;

  @Column(name = "currency_code", nullable = false, length = 3)
  private String currencyCode;

  @Column(nullable = false, length = 20)
  private String status;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @PrePersist
  void onCreate() {
    createdAt = Instant.now();
  }
}
