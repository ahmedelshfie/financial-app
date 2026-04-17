package com.example.finance.report.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class AccountResponse {
  private Long id;
  private BigDecimal currentBalance;
  private String currencyCode;
}
