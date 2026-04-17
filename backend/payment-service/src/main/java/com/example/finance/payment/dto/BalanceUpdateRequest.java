package com.example.finance.payment.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request DTO for balance update operations. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceUpdateRequest {

  private BigDecimal amount;
  private String operation;
}
