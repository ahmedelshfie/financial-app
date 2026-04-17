package com.example.finance.account.repository;

import com.example.finance.account.entity.AccountType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link AccountType} entities.
 *
 * <p>Inherits standard CRUD and pagination operations from {@link JpaRepository}. The {@link
 * #findByCode(String)} query method is used at account creation time to resolve the product type
 * from its business code.
 */
public interface AccountTypeRepository extends JpaRepository<AccountType, Long> {

  /**
   * Looks up an account type by its unique business code.
   *
   * @param code the product type code (e.g., {@code "CHECKING"}); case-sensitive
   * @return an {@link Optional} containing the matching type, or empty if not found
   */
  Optional<AccountType> findByCode(String code);
}
