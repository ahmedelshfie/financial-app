package com.example.finance.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity representing a bank account product type (e.g., CHECKING, SAVINGS).
 *
 * <p>Account types define the product rules that apply to all accounts of that kind, including
 * minimum balance requirements and daily transfer caps.
 *
 * <p>The {@code code} field is a short, unique business identifier used across services (e.g.,
 * {@code "CHECKING"}) and must not be changed after creation.
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "account_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountType {

  /** Auto-generated surrogate primary key. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Short, uppercase business code uniquely identifying this product type (e.g., {@code
   * "CHECKING"}, {@code "SAVINGS"}).
   */
  @Column(nullable = false, unique = true)
  private String code;

  /** Human-readable display name (e.g., {@code "Checking Account"}). */
  @Column(nullable = false)
  private String name;

  /**
   * Minimum balance that must be maintained at all times to avoid fees. Expressed in the account's
   * currency.
   */
  @Column(name = "minimum_balance", nullable = false)
  private BigDecimal minimumBalance;

  /**
   * Maximum aggregate debit amount allowed within a single calendar day. Expressed in the account's
   * currency.
   */
  @Column(name = "daily_transfer_limit", nullable = false)
  private BigDecimal dailyTransferLimit;
}
