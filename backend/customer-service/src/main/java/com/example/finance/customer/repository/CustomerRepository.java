package com.example.finance.customer.repository;

import com.example.finance.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link Customer} entities.
 *
 * <p>Provides standard CRUD operations inherited from {@link JpaRepository} plus custom existence
 * checks used during customer creation to enforce uniqueness.
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {

  /**
   * Checks whether a customer with the given email address already exists.
   *
   * @param email the email address to check
   * @return {@code true} if a customer with that email exists, {@code false} otherwise
   */
  boolean existsByEmail(String email);

  /**
   * Checks whether a customer with the given national ID already exists.
   *
   * @param nationalId the national ID to check
   * @return {@code true} if a customer with that national ID exists, {@code false} otherwise
   */
  boolean existsByNationalId(String nationalId);
}
