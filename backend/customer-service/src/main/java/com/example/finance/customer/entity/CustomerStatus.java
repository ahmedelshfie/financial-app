package com.example.finance.customer.entity;

/**
 * Represents the lifecycle status of a customer account.
 *
 * <ul>
 *   <li>{@link #ACTIVE} — Account is in good standing and fully operational.
 *   <li>{@link #INACTIVE} — Account exists but has been deactivated voluntarily.
 *   <li>{@link #SUSPENDED} — Account is temporarily blocked due to compliance or fraud concerns.
 * </ul>
 */
public enum CustomerStatus {

  /** Account is fully active and operational. */
  ACTIVE,

  /** Account has been deactivated but not deleted. */
  INACTIVE,

  /** Account is temporarily suspended pending investigation. */
  SUSPENDED
}
