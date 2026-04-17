package com.example.finance.dashboard.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class PaymentResponse {
  private Long id;
  private String paymentReference;
  private Long sourceAccountId;
  private Long beneficiaryId;
  private BigDecimal amount;
  private String currencyCode;
  private String status;
}
