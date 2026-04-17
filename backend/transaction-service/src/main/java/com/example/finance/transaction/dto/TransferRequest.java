package com.example.finance.transaction.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request DTO for fund transfer operations. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

  @NotNull(message = ValidationMessages.SOURCE_ACCOUNT_REQUIRED)
  private Long sourceAccountId;

  @NotNull(message = ValidationMessages.DEST_ACCOUNT_REQUIRED)
  private Long destinationAccountId;

  @NotNull(message = ValidationMessages.AMOUNT_REQUIRED)
  @Positive(message = ValidationMessages.AMOUNT_POSITIVE)
  private BigDecimal amount;
}
