package com.example.finance.payment.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Entity
@Table(name = "beneficiaries")
@Getter
@Setter
@NoArgsConstructor
public class Beneficiary {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "customer_id", nullable = false)
  private Long customerId;

  @Column(name = "name", nullable = false, length = 120)
  private String name;

  @Column(name = "account_number", nullable = false, length = 50)
  private String accountNumber;

  @Column(name = "bank_code", nullable = false, length = 30)
  private String bankCode;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Builder
  public Beneficiary(
      Long id, Long customerId, String name, String accountNumber, String bankCode, Instant createdAt) {
    this.id = id;
    this.customerId = customerId;
    this.name = name;
    this.accountNumber = accountNumber;
    this.bankCode = bankCode;
    this.createdAt = createdAt;
  }

  @PrePersist
  void onCreate() {
    createdAt = Instant.now();
  }
}
