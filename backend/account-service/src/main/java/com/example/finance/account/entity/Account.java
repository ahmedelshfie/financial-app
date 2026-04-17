package com.example.finance.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity representing a customer bank account.
 *
 * <p>Each account belongs to a single customer and is associated with an {@link AccountType}.
 * Balances are stored as {@link BigDecimal} to avoid floating-point precision errors in financial
 * calculations.
 *
 * <p>The {@code openedAt} timestamp is set automatically by the {@link #prePersist()} lifecycle
 * callback on first insert.
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

  /** Auto-generated surrogate primary key. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Unique human-readable account identifier (e.g., {@code ACC-A1B2C3D4E5}). */
  @Column(name = "account_number", nullable = false, unique = true)
  private String accountNumber;

  /** Identifier of the customer who owns this account. */
  @Column(name = "customer_id", nullable = false)
  private Long customerId;

  /** The product type of this account (e.g., CHECKING, SAVINGS). */
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "account_type_id", nullable = false)
  private AccountType accountType;

  /** ISO 4217 currency code (e.g., {@code USD}, {@code EUR}). */
  @Column(name = "currency_code", nullable = false)
  private String currencyCode;

  /** Ledger balance including all settled transactions. */
  @Column(name = "current_balance", nullable = false)
  private BigDecimal currentBalance;

  /**
   * Balance available for new transactions after reserving pending debits. May differ from {@link
   * #currentBalance} when holds are active.
   */
  @Column(name = "available_balance", nullable = false)
  private BigDecimal availableBalance;

  /** Account lifecycle status: {@code ACTIVE}, {@code FROZEN}, or {@code CLOSED}. */
  @Column(nullable = false)
  private String status;

  /** Timestamp (UTC) of when this account was first persisted. */
  @Column(name = "opened_at")
  private Instant openedAt;

  /**
   * Sets {@link #openedAt} to the current UTC instant before the entity is first inserted into the
   * database.
   */
  @PrePersist
  void prePersist() {
    openedAt = Instant.now();
  }
}
