package com.example.finance.account.service.impl;

import com.example.finance.account.dto.AccountResponse;
import com.example.finance.account.dto.BalanceUpdateRequest;
import com.example.finance.account.dto.CreateAccountRequest;
import com.example.finance.account.dto.OperationType;
import com.example.finance.account.entity.Account;
import com.example.finance.account.entity.AccountType;
import com.example.finance.account.exception.AccountNotFoundException;
import com.example.finance.account.exception.ErrorCatalog;
import com.example.finance.account.exception.InsufficientFundsException;
import com.example.finance.account.mapper.AccountMapper;
import com.example.finance.account.repository.AccountRepository;
import com.example.finance.account.repository.AccountTypeRepository;
import com.example.finance.account.service.AccountService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link AccountService}.
 *
 * <p>Write operations run within a full read-write transaction; read-only operations are annotated
 * with {@code readOnly = true} to allow the JPA provider and database to apply query optimisations
 * such as skipping dirty-checking.
 */
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

  /** Prefix prepended to every generated account number. */
  private static final String ACCOUNT_NUMBER_PREFIX = "ACC-";

  /** Number of characters (from the UUID) used in the account number suffix. */
  private static final int ACCOUNT_NUMBER_SUFFIX_LENGTH = 10;

  private static final int MAX_ACCOUNT_NUMBER_ATTEMPTS = 5;

  /** Initial lifecycle status assigned to every new account. */
  private static final String INITIAL_STATUS = "ACTIVE";

  private final AccountRepository accountRepository;
  private final AccountTypeRepository typeRepository;
  private final AccountMapper accountMapper;

  /**
   * {@inheritDoc}
   *
   * <p>Generates a random account number with the format {@code ACC-XXXXXXXXXX} and persists the
   * account with zero balances and {@code ACTIVE} status.
   */
  @Override
  @Transactional
  public AccountResponse create(CreateAccountRequest request) {
    String normalizedTypeCode = request.getAccountTypeCode().trim().toUpperCase(Locale.ROOT);
    String normalizedCurrencyCode = request.getCurrencyCode().trim().toUpperCase(Locale.ROOT);

    AccountType type =
        typeRepository
            .findByCode(normalizedTypeCode)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        ErrorCatalog.ACCOUNT_TYPE_NOT_FOUND.message(normalizedTypeCode)));

    @SuppressWarnings("null")
    Account account =
        accountRepository.save(
            Account.builder()
                .accountNumber(generateUniqueAccountNumber())
                .customerId(request.getCustomerId())
                .accountType(type)
                .currencyCode(normalizedCurrencyCode)
                .currentBalance(BigDecimal.ZERO)
                .availableBalance(BigDecimal.ZERO)
                .status(INITIAL_STATUS)
                .build());

    return accountMapper.toResponse(account);
  }

  /**
   * {@inheritDoc}
   *
   * @throws AccountNotFoundException if no account with the given {@code id} exists
   */
  @Override
  @Transactional(readOnly = true)
  public AccountResponse getById(Long id) {
    @SuppressWarnings("null")
    Account account =
        accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    return accountMapper.toResponse(account);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public List<AccountResponse> findByCustomerId(Long customerId) {
    return accountRepository.findByCustomerId(customerId).stream()
        .map(accountMapper::toResponse)
        .toList();
  }

  /**
   * {@inheritDoc}
   *
   * <p>Both {@code currentBalance} and {@code availableBalance} are set to the same post-operation
   * value. Future enhancements may introduce hold management that differentiates the two fields.
   *
   * @throws AccountNotFoundException if no account with the given {@code id} exists
   * @throws InsufficientFundsException if the operation is {@link OperationType#DEBIT} and the
   *     amount exceeds the available balance
   */
  @Override
  @Transactional
  public AccountResponse updateBalance(Long id, BalanceUpdateRequest request) {
    @SuppressWarnings("null")
    Account account =
        accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));

    BigDecimal newBalance =
        switch (request.getOperation()) {
          case DEBIT -> {
            if (account.getAvailableBalance().compareTo(request.getAmount()) < 0) {
              throw new InsufficientFundsException();
            }
            yield account.getCurrentBalance().subtract(request.getAmount());
          }
          case CREDIT -> account.getCurrentBalance().add(request.getAmount());
        };

    account.setCurrentBalance(newBalance);
    account.setAvailableBalance(newBalance);
    accountRepository.save(account);

    return accountMapper.toResponse(account);
  }

  // ─── Private helpers ──────────────────────────────────────────────────────

  /**
   * Generates a random, unique account number in the format {@code ACC-XXXXXXXXXX} where {@code X}
   * is an uppercase alphanumeric character derived from a random UUID.
   *
   * @return the generated account number string
   */
  private String generateAccountNumber() {
    return ACCOUNT_NUMBER_PREFIX
        + UUID.randomUUID()
            .toString()
            .replace("-", "")
            .substring(0, ACCOUNT_NUMBER_SUFFIX_LENGTH)
            .toUpperCase();
  }

  private String generateUniqueAccountNumber() {
    for (int attempt = 0; attempt < MAX_ACCOUNT_NUMBER_ATTEMPTS; attempt++) {
      String accountNumber = generateAccountNumber();
      if (!accountRepository.existsByAccountNumber(accountNumber)) {
        return accountNumber;
      }
    }
    throw new IllegalStateException(ErrorCatalog.ACCOUNT_NUMBER_GENERATION_FAILED.message());
  }
}
