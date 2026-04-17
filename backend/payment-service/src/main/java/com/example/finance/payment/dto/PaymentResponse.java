package com.example.finance.payment.dto;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {
  private Long id;
  private String paymentReference;
  private Long sourceAccountId;
  private Long beneficiaryId;
  private BigDecimal amount;
  private String currencyCode;
  private String status;
  private Instant createdAt;
}
