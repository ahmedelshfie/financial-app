package com.example.finance.payment.dto;

import java.math.BigDecimal;
import lombok.Data;

/** Response DTO for account details. */
@Data
public class AccountResponse {

  private Long id;
  private String accountNumber;
  private Long customerId;
  private String accountTypeCode;
  private String currencyCode;
  private BigDecimal currentBalance;
  private BigDecimal availableBalance;
  private String status;
}
