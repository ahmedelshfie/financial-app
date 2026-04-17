package com.example.finance.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Entity representing a financial transaction. */
@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "transaction_reference", nullable = false, unique = true, length = 50)
  private String transactionReference;

  @Column(name = "source_account_id")
  private Long sourceAccountId;

  @Column(name = "destination_account_id")
  private Long destinationAccountId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "transaction_type_id", nullable = false)
  private TransactionType transactionType;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal amount;

  @Column(name = "currency_code", nullable = false, length = 3)
  private String currencyCode;

  @Column(nullable = false, length = 20)
  private String status;

  @Column(name = "description", length = 512)
  private String description;

  @Column(name = "initiated_by", length = 255)
  private String initiatedBy;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @PrePersist
  void onCreate() {
    createdAt = Instant.now();
  }
}
