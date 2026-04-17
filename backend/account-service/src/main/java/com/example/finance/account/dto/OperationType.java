package com.example.finance.account.dto;

/**
 * Represents the direction of a balance update operation on a bank account.
 *
 * <p>Using an enum instead of a raw {@code String} ensures that only valid operation values can be
 * submitted to the API. Jackson deserializes JSON string values (e.g., {@code "DEBIT"}) into the
 * corresponding enum constant automatically; an unrecognised value causes a {@code 400 Bad Request}
 * before the request ever reaches service logic.
 *
 * <p>Values are case-sensitive in JSON: {@code "DEBIT"} and {@code "CREDIT"} are accepted; {@code
 * "debit"} is rejected unless Jackson is configured with {@code
 * MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS}.
 */
public enum OperationType {

  /**
   * Decreases the account balance by the specified amount. Rejected when the available balance is
   * insufficient.
   */
  DEBIT,

  /**
   * Increases the account balance by the specified amount. Always permitted regardless of the
   * current balance.
   */
  CREDIT
}
