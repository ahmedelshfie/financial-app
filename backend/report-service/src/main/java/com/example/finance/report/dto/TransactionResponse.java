package com.example.finance.report.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response DTO for transaction details. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
  private Long id;
  private String transactionReference;
  private Long sourceAccountId;
  private Long destinationAccountId;
  private String transactionType;
  private BigDecimal amount;
  private String currencyCode;
  private String status;
  private String description;
}
