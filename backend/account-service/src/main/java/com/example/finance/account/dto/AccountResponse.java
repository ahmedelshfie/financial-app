package com.example.finance.account.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

/**
 * Immutable read model returned by account endpoints.
 *
 * <p>This DTO is assembled by the service layer from the {@code Account} entity and intentionally
 * contains only the subset of fields safe to expose over the API. Sensitive internal fields (e.g.,
 * database foreign keys, audit metadata) are excluded.
 */
@Data
@Builder
public class AccountResponse {

  /** Unique surrogate identifier of the account. */
  private Long id;

  /** Human-readable account number (e.g., {@code "ACC-A1B2C3D4E5"}). */
  private String accountNumber;

  /** Identifier of the customer who owns this account. */
  private Long customerId;

  /** Business code of the account product type (e.g., {@code "CHECKING"}). */
  private String accountTypeCode;

  /** ISO 4217 currency code (e.g., {@code "USD"}). */
  private String currencyCode;

  /** Settled ledger balance including all posted transactions. */
  private BigDecimal currentBalance;

  /** Balance available for new transactions after reserving pending debits. */
  private BigDecimal availableBalance;

  /** Current lifecycle status: {@code ACTIVE}, {@code FROZEN}, or {@code CLOSED}. */
  private String status;
}
