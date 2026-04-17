package com.example.finance.customer.entity;

/**
 * Represents the Know-Your-Customer (KYC) verification status of a customer.
 *
 * <ul>
 *   <li>{@link #PENDING} — KYC documents submitted but not yet reviewed.
 *   <li>{@link #VERIFIED} — KYC verification was successful; customer is fully onboarded.
 *   <li>{@link #REJECTED} — KYC documents were reviewed and rejected; customer must resubmit.
 * </ul>
 */
public enum KycStatus {

  /** KYC verification is awaiting review. */
  PENDING,

  /** KYC has been reviewed and approved. */
  VERIFIED,

  /** KYC documents were rejected. */
  REJECTED
}
