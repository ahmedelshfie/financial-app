package com.example.finance.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * Request payload for creating a new bank account.
 *
 * <p>All fields are required. The {@code accountTypeCode} must match an existing {@code
 * AccountType.code} value, and {@code currencyCode} must be a valid ISO 4217 currency code (e.g.,
 * {@code "USD"}).
 */
@Data
public class CreateAccountRequest {

  /**
   * Identifier of the customer who will own the account. Must be a positive value greater than
   * zero.
   */
  @NotNull(message = ValidationMessages.CUSTOMER_ID_REQUIRED)
  @Positive(message = ValidationMessages.CUSTOMER_ID_POSITIVE)
  private Long customerId;

  /**
   * Business code of the account type to create (e.g., {@code "CHECKING"}). Must match an existing
   * {@code AccountType} record.
   */
  @NotBlank(message = ValidationMessages.ACCOUNT_TYPE_REQUIRED)
  @Pattern(regexp = "^[A-Za-z_]{2,30}$", message = ValidationMessages.ACCOUNT_TYPE_FORMAT)
  private String accountTypeCode;

  /** ISO 4217 currency code for the account (e.g., {@code "USD"}, {@code "EUR"}). */
  @NotBlank(message = ValidationMessages.CURRENCY_REQUIRED)
  @Pattern(regexp = "^[A-Za-z]{3}$", message = ValidationMessages.CURRENCY_FORMAT)
  private String currencyCode;
}
