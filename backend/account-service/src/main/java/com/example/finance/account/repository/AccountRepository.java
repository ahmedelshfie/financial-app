package com.example.finance.account.repository;

import com.example.finance.account.entity.Account;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link Account} entities.
 *
 * <p>Inherits standard CRUD and pagination operations from {@link JpaRepository}. Custom query
 * methods are derived automatically from their method names by Spring Data at startup.
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

  /**
   * Returns all accounts belonging to the specified customer.
   *
   * @param customerId the customer's unique identifier
   * @return a (possibly empty) list of accounts; never {@code null}
   */
  List<Account> findByCustomerId(Long customerId);

  boolean existsByAccountNumber(String accountNumber);
}
