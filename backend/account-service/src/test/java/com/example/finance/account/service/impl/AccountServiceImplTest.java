package com.example.finance.account.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.finance.account.dto.AccountResponse;
import com.example.finance.account.dto.BalanceUpdateRequest;
import com.example.finance.account.dto.CreateAccountRequest;
import com.example.finance.account.dto.OperationType;
import com.example.finance.account.entity.Account;
import com.example.finance.account.entity.AccountType;
import com.example.finance.account.exception.AccountNotFoundException;
import com.example.finance.account.exception.InsufficientFundsException;
import com.example.finance.account.mapper.AccountMapper;
import com.example.finance.account.repository.AccountRepository;
import com.example.finance.account.repository.AccountTypeRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link AccountServiceImpl}.
 *
 * <p>All dependencies are replaced by Mockito mocks so that tests run without a database or Spring
 * context, keeping execution time low.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AccountServiceImpl unit tests")
class AccountServiceImplTest {

  @Mock private AccountRepository accountRepository;

  @Mock private AccountTypeRepository typeRepository;

  @Spy private AccountMapper accountMapper = new AccountMapper();

  @InjectMocks private AccountServiceImpl service;

  private AccountType accountType;
  private Account account;

  @BeforeEach
  void setUp() {
    accountType =
        AccountType.builder()
            .id(1L)
            .code("CHECKING")
            .name("Checking Account")
            .minimumBalance(BigDecimal.ZERO)
            .dailyTransferLimit(new BigDecimal("5000"))
            .build();

    account =
        Account.builder()
            .id(10L)
            .accountNumber("ACC-ABCDE12345")
            .customerId(99L)
            .accountType(accountType)
            .currencyCode("USD")
            .currentBalance(BigDecimal.ZERO)
            .availableBalance(BigDecimal.ZERO)
            .status("ACTIVE")
            .build();
  }

  // ─── create ───────────────────────────────────────────────────────────────

  @Nested
  @DisplayName("create()")
  class CreateTests {

    @SuppressWarnings("null")
    @Test
    @DisplayName("happy path: saves account with ACTIVE status and returns mapped response")
    void create_success() {
      CreateAccountRequest request = new CreateAccountRequest();
      request.setCustomerId(99L);
      request.setAccountTypeCode("CHECKING");
      request.setCurrencyCode("USD");

      when(typeRepository.findByCode("CHECKING")).thenReturn(Optional.of(accountType));
      when(accountRepository.save(any(Account.class))).thenReturn(account);

      AccountResponse response = service.create(request);

      assertThat(response.getId()).isEqualTo(10L);
      assertThat(response.getAccountNumber()).isEqualTo("ACC-ABCDE12345");
      assertThat(response.getCustomerId()).isEqualTo(99L);
      assertThat(response.getAccountTypeCode()).isEqualTo("CHECKING");
      assertThat(response.getCurrencyCode()).isEqualTo("USD");
      assertThat(response.getCurrentBalance()).isEqualByComparingTo(BigDecimal.ZERO);
      assertThat(response.getStatus()).isEqualTo("ACTIVE");

      // Verify entity passed to save has correct fields
      ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
      verify(accountRepository).save(captor.capture());
      Account saved = captor.getValue();
      assertThat(saved.getStatus()).isEqualTo("ACTIVE");
      assertThat(saved.getCustomerId()).isEqualTo(99L);
      assertThat(saved.getCurrentBalance()).isEqualByComparingTo(BigDecimal.ZERO);
      assertThat(saved.getAvailableBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("generates account number matching ACC-[A-Z0-9]{10} format")
    void create_generatedAccountNumberFormat() {
      CreateAccountRequest request = new CreateAccountRequest();
      request.setCustomerId(1L);
      request.setAccountTypeCode("CHECKING");
      request.setCurrencyCode("USD");

      when(typeRepository.findByCode("CHECKING")).thenReturn(Optional.of(accountType));
      when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

      service.create(request);

      ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
      verify(accountRepository).save(captor.capture());
      String accountNumber = captor.getValue().getAccountNumber();
      assertThat(accountNumber).matches("ACC-[A-Z0-9]{10}");
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("unknown account type code throws IllegalArgumentException")
    void create_unknownType_throwsIllegalArgument() {
      CreateAccountRequest request = new CreateAccountRequest();
      request.setCustomerId(1L);
      request.setAccountTypeCode("UNKNOWN");
      request.setCurrencyCode("USD");

      when(typeRepository.findByCode("UNKNOWN")).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.create(request))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Account type not found");

      verify(accountRepository, never()).save(any());
    }
  }

  // ─── getById ──────────────────────────────────────────────────────────────

  @Nested
  @DisplayName("getById()")
  class GetByIdTests {

    @Test
    @DisplayName("existing id returns AccountResponse with all fields mapped")
    void getById_success() {
      when(accountRepository.findById(10L)).thenReturn(Optional.of(account));

      AccountResponse response = service.getById(10L);

      assertThat(response.getId()).isEqualTo(10L);
      assertThat(response.getAccountNumber()).isEqualTo("ACC-ABCDE12345");
      assertThat(response.getCustomerId()).isEqualTo(99L);
      assertThat(response.getCurrencyCode()).isEqualTo("USD");
      assertThat(response.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("missing id throws AccountNotFoundException")
    void getById_notFound_throwsAccountNotFoundException() {
      when(accountRepository.findById(999L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.getById(999L))
          .isInstanceOf(AccountNotFoundException.class)
          .hasMessageContaining("999");
    }
  }

  // ─── findByCustomerId ─────────────────────────────────────────────────────

  @Nested
  @DisplayName("findByCustomerId()")
  class FindByCustomerIdTests {

    @Test
    @DisplayName("returns mapped list in original order")
    void findByCustomerId_returnsList() {
      Account second =
          Account.builder()
              .id(11L)
              .accountNumber("ACC-XXXXXXXXXX")
              .customerId(99L)
              .accountType(accountType)
              .currencyCode("EUR")
              .currentBalance(BigDecimal.TEN)
              .availableBalance(BigDecimal.TEN)
              .status("ACTIVE")
              .build();

      when(accountRepository.findByCustomerId(99L)).thenReturn(List.of(account, second));

      List<AccountResponse> result = service.findByCustomerId(99L);

      assertThat(result).hasSize(2);
      assertThat(result.get(0).getId()).isEqualTo(10L);
      assertThat(result.get(0).getCurrencyCode()).isEqualTo("USD");
      assertThat(result.get(1).getId()).isEqualTo(11L);
      assertThat(result.get(1).getCurrencyCode()).isEqualTo("EUR");
    }

    @Test
    @DisplayName("no accounts returns empty list")
    void findByCustomerId_noAccounts_returnsEmpty() {
      when(accountRepository.findByCustomerId(1L)).thenReturn(List.of());

      assertThat(service.findByCustomerId(1L)).isEmpty();
    }
  }

  // ─── updateBalance ────────────────────────────────────────────────────────

  @Nested
  @DisplayName("updateBalance()")
  class UpdateBalanceTests {

    @SuppressWarnings("null")
    @Test
    @DisplayName("CREDIT increases both balances and saves account")
    void updateBalance_credit_success() {
      account.setCurrentBalance(new BigDecimal("100.00"));
      account.setAvailableBalance(new BigDecimal("100.00"));

      when(accountRepository.findById(10L)).thenReturn(Optional.of(account));
      when(accountRepository.save(account)).thenReturn(account);

      BalanceUpdateRequest req = new BalanceUpdateRequest();
      req.setAmount(new BigDecimal("50.00"));
      req.setOperation(OperationType.CREDIT);

      AccountResponse response = service.updateBalance(10L, req);

      assertThat(response.getCurrentBalance()).isEqualByComparingTo("150.00");
      assertThat(response.getAvailableBalance()).isEqualByComparingTo("150.00");
      verify(accountRepository).save(account);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("DEBIT decreases both balances when funds are sufficient")
    void updateBalance_debit_success() {
      account.setCurrentBalance(new BigDecimal("200.00"));
      account.setAvailableBalance(new BigDecimal("200.00"));

      when(accountRepository.findById(10L)).thenReturn(Optional.of(account));
      when(accountRepository.save(account)).thenReturn(account);

      BalanceUpdateRequest req = new BalanceUpdateRequest();
      req.setAmount(new BigDecimal("75.00"));
      req.setOperation(OperationType.DEBIT);

      AccountResponse response = service.updateBalance(10L, req);

      assertThat(response.getCurrentBalance()).isEqualByComparingTo("125.00");
      assertThat(response.getAvailableBalance()).isEqualByComparingTo("125.00");
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("DEBIT throws InsufficientFundsException when amount exceeds available balance")
    void updateBalance_debit_insufficientFunds() {
      account.setCurrentBalance(new BigDecimal("10.00"));
      account.setAvailableBalance(new BigDecimal("10.00"));

      when(accountRepository.findById(10L)).thenReturn(Optional.of(account));

      BalanceUpdateRequest req = new BalanceUpdateRequest();
      req.setAmount(new BigDecimal("50.00"));
      req.setOperation(OperationType.DEBIT);

      assertThatThrownBy(() -> service.updateBalance(10L, req))
          .isInstanceOf(InsufficientFundsException.class)
          .hasMessageContaining("Insufficient funds");

      verify(accountRepository, never()).save(any());
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("DEBIT with amount exactly equal to available balance succeeds")
    void updateBalance_debit_exactBalance_succeeds() {
      account.setCurrentBalance(new BigDecimal("50.00"));
      account.setAvailableBalance(new BigDecimal("50.00"));

      when(accountRepository.findById(10L)).thenReturn(Optional.of(account));
      when(accountRepository.save(account)).thenReturn(account);

      BalanceUpdateRequest req = new BalanceUpdateRequest();
      req.setAmount(new BigDecimal("50.00"));
      req.setOperation(OperationType.DEBIT);

      AccountResponse response = service.updateBalance(10L, req);

      assertThat(response.getCurrentBalance()).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("account not found throws AccountNotFoundException")
    void updateBalance_notFound_throwsAccountNotFoundException() {
      when(accountRepository.findById(999L)).thenReturn(Optional.empty());

      BalanceUpdateRequest req = new BalanceUpdateRequest();
      req.setAmount(BigDecimal.ONE);
      req.setOperation(OperationType.CREDIT);

      assertThatThrownBy(() -> service.updateBalance(999L, req))
          .isInstanceOf(AccountNotFoundException.class)
          .hasMessageContaining("999");
    }
  }
}
