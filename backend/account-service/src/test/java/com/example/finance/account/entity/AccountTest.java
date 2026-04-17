package com.example.finance.account.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Account} JPA entity.
 *
 * <p>Tests verify Lombok-generated builder & accessor behaviour and the {@code @PrePersist}
 * lifecycle callback without needing a database.
 */
@DisplayName("Account entity tests")
class AccountTest {

  @Test
  @DisplayName("builder sets all fields correctly")
  void builder_setsAllFields() {
    AccountType type =
        AccountType.builder()
            .id(1L)
            .code("CHECKING")
            .name("Checking Account")
            .minimumBalance(BigDecimal.ZERO)
            .dailyTransferLimit(new BigDecimal("5000"))
            .build();

    Account account =
        Account.builder()
            .id(10L)
            .accountNumber("ACC-ABCDE12345")
            .customerId(99L)
            .accountType(type)
            .currencyCode("USD")
            .currentBalance(new BigDecimal("100.00"))
            .availableBalance(new BigDecimal("80.00"))
            .status("ACTIVE")
            .build();

    assertThat(account.getId()).isEqualTo(10L);
    assertThat(account.getAccountNumber()).isEqualTo("ACC-ABCDE12345");
    assertThat(account.getCustomerId()).isEqualTo(99L);
    assertThat(account.getAccountType()).isEqualTo(type);
    assertThat(account.getCurrencyCode()).isEqualTo("USD");
    assertThat(account.getCurrentBalance()).isEqualByComparingTo("100.00");
    assertThat(account.getAvailableBalance()).isEqualByComparingTo("80.00");
    assertThat(account.getStatus()).isEqualTo("ACTIVE");
  }

  @Test
  @DisplayName("no-args constructor creates empty instance")
  void noArgsConstructor_createsEmptyInstance() {
    Account account = new Account();
    assertThat(account.getId()).isNull();
    assertThat(account.getAccountNumber()).isNull();
    assertThat(account.getStatus()).isNull();
  }

  @Test
  @DisplayName("setter methods update fields correctly")
  void setters_updateFields() {
    Account account = new Account();
    account.setStatus("FROZEN");
    account.setCurrentBalance(BigDecimal.TEN);

    assertThat(account.getStatus()).isEqualTo("FROZEN");
    assertThat(account.getCurrentBalance()).isEqualByComparingTo(BigDecimal.TEN);
  }

  @Test
  @DisplayName("prePersist sets openedAt to a recent non-null instant")
  void prePersist_setsOpenedAt() {
    Account account = new Account();
    assertThat(account.getOpenedAt()).isNull();

    Instant before = Instant.now();
    account.prePersist();
    Instant after = Instant.now();

    assertThat(account.getOpenedAt()).isNotNull().isAfterOrEqualTo(before).isBeforeOrEqualTo(after);
  }

  @Test
  @DisplayName("all-args constructor sets all fields")
  void allArgsConstructor_setsAllFields() {
    AccountType type = AccountType.builder().id(2L).code("SAVINGS").build();
    Instant now = Instant.now();

    Account account =
        new Account(5L, "ACC-XYZ", 7L, type, "EUR", BigDecimal.ONE, BigDecimal.ONE, "ACTIVE", now);

    assertThat(account.getId()).isEqualTo(5L);
    assertThat(account.getAccountNumber()).isEqualTo("ACC-XYZ");
    assertThat(account.getOpenedAt()).isEqualTo(now);
  }
}
