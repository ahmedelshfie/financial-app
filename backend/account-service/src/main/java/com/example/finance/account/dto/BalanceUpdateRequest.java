package com.example.finance.account.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;

/**
 * Request payload for applying a debit or credit operation to an account.
 *
 * <p>The {@code operation} field uses the {@link OperationType} enum, which guarantees that only
 * {@code DEBIT} or {@code CREDIT} values are accepted at the Jackson deserialization layer — before
 * any service logic is reached. An unrecognised string results in a {@code 400 Bad Request}
 * automatically.
 *
 * <p>The {@code amount} must be strictly positive; zero and negative amounts are rejected by Bean
 * Validation before the request reaches the service layer.
 */
@Data
public class BalanceUpdateRequest {

  /**
   * Monetary amount to apply to the account balance. Must be strictly positive (greater than zero).
   */
  @NotNull(message = ValidationMessages.AMOUNT_REQUIRED)
  @Positive(message = ValidationMessages.AMOUNT_POSITIVE)
  private BigDecimal amount;

  /**
   * Direction of the balance update. Accepted values: {@link OperationType#DEBIT} or {@link
   * OperationType#CREDIT}. Invalid strings are rejected during JSON deserialization with HTTP
   * {@code 400}.
   */
  @NotNull(message = ValidationMessages.OPERATION_REQUIRED)
  private OperationType operation;
}
