package com.example.finance.customer.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

/**
 * JPA entity representing a customer in the financial system.
 *
 * <p>Each customer has a unique customer code, email, and national ID. The status and KYC status
 * fields track the customer's onboarding progress.
 */
@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Customer {

  /** Auto-generated surrogate primary key. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Unique customer identifier (e.g., {@code CUST-ABC123}). */
  @Column(name = "customer_code", nullable = false, unique = true)
  private String customerCode;

  /** Customer's first name. */
  @Column(name = "first_name", nullable = false)
  private String firstName;

  /** Customer's last name. */
  @Column(name = "last_name", nullable = false)
  private String lastName;

  /** Unique email address for communication and login. */
  @Column(nullable = false, unique = true)
  private String email;

  /** National identification number (unique per customer). */
  @Column(name = "national_id", nullable = false, unique = true)
  private String nationalId;

  /** Customer lifecycle status: {@code ACTIVE}, {@code INACTIVE}, or {@code SUSPENDED}. */
  @Column(nullable = false)
  private String status;

  /** KYC verification status: {@code PENDING}, {@code VERIFIED}, or {@code REJECTED}. */
  @Column(name = "kyc_status", nullable = false)
  private String kycStatus;

  /** Timestamp (UTC) when this customer record was created. */
  @Column(name = "created_at")
  private Instant createdAt;

  /**
   * Sets {@link #createdAt} to the current UTC instant before the entity is first inserted into the
   * database.
   */
  @PrePersist
  void prePersist() {
    createdAt = Instant.now();
  }
}
