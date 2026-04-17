package com.example.finance.account.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link AccountType} JPA entity.
 *
 * <p>Verifies that Lombok-generated builder and accessor methods work as expected without requiring
 * a Spring context or database connection.
 */
@DisplayName("AccountType entity tests")
class AccountTypeTest {

  @Test
  @DisplayName("builder sets all fields correctly")
  void builder_setsAllFields() {
    AccountType type =
        AccountType.builder()
            .id(1L)
            .code("CHECKING")
            .name("Checking Account")
            .minimumBalance(BigDecimal.ZERO)
            .dailyTransferLimit(new BigDecimal("5000.00"))
            .build();

    assertThat(type.getId()).isEqualTo(1L);
    assertThat(type.getCode()).isEqualTo("CHECKING");
    assertThat(type.getName()).isEqualTo("Checking Account");
    assertThat(type.getMinimumBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    assertThat(type.getDailyTransferLimit()).isEqualByComparingTo("5000.00");
  }

  @Test
  @DisplayName("no-args constructor creates empty instance")
  void noArgsConstructor_createsEmptyInstance() {
    AccountType type = new AccountType();
    assertThat(type.getId()).isNull();
    assertThat(type.getCode()).isNull();
  }

  @Test
  @DisplayName("setter methods update fields correctly")
  void setters_updateFields() {
    AccountType type = new AccountType();
    type.setCode("SAVINGS");
    type.setName("Savings Account");

    assertThat(type.getCode()).isEqualTo("SAVINGS");
    assertThat(type.getName()).isEqualTo("Savings Account");
  }

  @Test
  @DisplayName("all-args constructor sets all fields")
  void allArgsConstructor_setsAllFields() {
    AccountType type =
        new AccountType(
            3L, "FIXED", "Fixed Deposit", new BigDecimal("1000.00"), new BigDecimal("10000.00"));

    assertThat(type.getId()).isEqualTo(3L);
    assertThat(type.getCode()).isEqualTo("FIXED");
    assertThat(type.getName()).isEqualTo("Fixed Deposit");
    assertThat(type.getMinimumBalance()).isEqualByComparingTo("1000.00");
    assertThat(type.getDailyTransferLimit()).isEqualByComparingTo("10000.00");
  }
}
