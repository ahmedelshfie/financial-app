package com.example.finance.dashboard.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
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
