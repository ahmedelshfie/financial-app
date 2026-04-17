package com.example.finance.payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class PaymentRequest {
  @NotNull(message = ValidationMessages.SOURCE_ACCOUNT_REQUIRED)
  private Long sourceAccountId;

  @NotNull(message = ValidationMessages.BENEFICIARY_REQUIRED)
  private Long beneficiaryId;

  @NotNull(message = ValidationMessages.AMOUNT_REQUIRED)
  @Positive(message = ValidationMessages.AMOUNT_POSITIVE)
  private BigDecimal amount;
}
