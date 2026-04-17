package com.example.finance.payment.service.impl;

import com.example.finance.payment.client.AccountClient;
import com.example.finance.payment.dto.AccountResponse;
import com.example.finance.payment.dto.BalanceUpdateRequest;
import com.example.finance.payment.dto.TransactionResponse;
import com.example.finance.payment.dto.TransferRequest;
import com.example.finance.payment.entity.Transaction;
import com.example.finance.payment.entity.TransactionType;
import com.example.finance.payment.exception.ErrorCatalog;
import com.example.finance.payment.repository.TransactionRepository;
import com.example.finance.payment.repository.TransactionTypeRepository;
import com.example.finance.payment.service.TransactionService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implementation of {@link TransactionService} handling business logic for transactions. */
@Service
public class TransactionServiceImpl implements TransactionService {

  private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);
  private static final String TRANSACTION_REFERENCE_PREFIX = "TXN-";
  private static final String TRANSFER_TYPE_CODE = "TRANSFER";
  private static final String COMPLETED_STATUS = "COMPLETED";
  private static final String TRANSFER_DESCRIPTION = "Funds transfer";

  private final TransactionRepository repository;
  private final TransactionTypeRepository typeRepository;
  private final AccountClient accountClient;

  public TransactionServiceImpl(
      TransactionRepository repository,
      TransactionTypeRepository typeRepository,
      AccountClient accountClient) {
    this.repository = repository;
    this.typeRepository = typeRepository;
    this.accountClient = accountClient;
  }

  @Override
  @Transactional
  public TransactionResponse transfer(TransferRequest request, String token) {
    log.info(
        "Initiating transfer from account {} to account {} for amount {}",
        request.getSourceAccountId(),
        request.getDestinationAccountId(),
        request.getAmount());

    validateTransferRequest(request);

    AccountResponse source = fetchAccount(request.getSourceAccountId(), token);
    AccountResponse destination = fetchAccount(request.getDestinationAccountId(), token);

    validateAccounts(source, destination);
    validateCurrencyCompatibility(source, destination);

    updateAccountBalances(source, destination, request.getAmount(), token);

    TransactionType transferType = getTransferTransactionType();

    Transaction transaction =
        createAndSaveTransaction(source, destination, transferType, request.getAmount());

    log.info(
        "Transfer completed successfully with reference: {}",
        transaction.getTransactionReference());
    return mapToResponse(transaction);
  }

  @Override
  public List<TransactionResponse> history(Long accountId) {
    log.debug("Fetching transaction history for account: {}", accountId);
    return repository.findBySourceAccountIdOrDestinationAccountId(accountId, accountId).stream()
        .map(this::mapToResponse)
        .toList();
  }

  private void validateTransferRequest(TransferRequest request) {
    if (Objects.equals(request.getSourceAccountId(), request.getDestinationAccountId())) {
      log.warn("Transfer rejected: source and destination accounts are the same");
      throw new IllegalArgumentException(ErrorCatalog.SAME_ACCOUNT_TRANSFER.message());
    }
  }

  private AccountResponse fetchAccount(Long accountId, String token) {
    AccountResponse account = accountClient.getAccount(accountId, token);
    if (account == null) {
      log.warn("Account not found: {}", accountId);
      throw new IllegalArgumentException(ErrorCatalog.ACCOUNT_NOT_FOUND.message(accountId));
    }
    return account;
  }

  private void validateAccounts(AccountResponse source, AccountResponse destination) {
    if (source == null || destination == null) {
      log.warn("Transfer rejected: one or both accounts not found");
      throw new IllegalArgumentException(ErrorCatalog.ACCOUNT_NOT_FOUND_GENERIC.message());
    }
  }

  private void validateCurrencyCompatibility(AccountResponse source, AccountResponse destination) {
    if (!source.getCurrencyCode().equals(destination.getCurrencyCode())) {
      log.warn(
          "Transfer rejected: cross-currency transfer not supported ({} vs {})",
          source.getCurrencyCode(),
          destination.getCurrencyCode());
      throw new IllegalArgumentException(ErrorCatalog.UNSUPPORTED_CURRENCY_TRANSFER.message());
    }
  }

  private void updateAccountBalances(
      AccountResponse source, AccountResponse destination, BigDecimal amount, String token) {
    log.debug("Debiting account {} with amount {}", source.getId(), amount);
    accountClient.updateBalance(source.getId(), new BalanceUpdateRequest(amount, "DEBIT"), token);

    log.debug("Crediting account {} with amount {}", destination.getId(), amount);
    accountClient.updateBalance(
        destination.getId(), new BalanceUpdateRequest(amount, "CREDIT"), token);
  }

  private TransactionType getTransferTransactionType() {
    return typeRepository
        .findByCode(TRANSFER_TYPE_CODE)
        .orElseThrow(
            () -> {
              log.error("Transaction type '{}' not found in database", TRANSFER_TYPE_CODE);
              return new IllegalStateException(
                  ErrorCatalog.TRANSACTION_TYPE_NOT_FOUND.message(TRANSFER_TYPE_CODE));
            });
  }

  @SuppressWarnings("null")
  private Transaction createAndSaveTransaction(
      AccountResponse source,
      AccountResponse destination,
      TransactionType type,
      BigDecimal amount) {
    String transactionReference = generateTransactionReference();
    String initiatedBy = resolveInitiatedBy();

    Transaction transaction =
        Transaction.builder()
            .transactionReference(transactionReference)
            .sourceAccountId(source.getId())
            .destinationAccountId(destination.getId())
            .transactionType(type)
            .amount(amount)
            .currencyCode(source.getCurrencyCode())
            .status(COMPLETED_STATUS)
            .description(TRANSFER_DESCRIPTION)
            .initiatedBy(initiatedBy)
            .build();

    return repository.save(transaction);
  }

  private String generateTransactionReference() {
    return TRANSACTION_REFERENCE_PREFIX
        + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
  }

  private String resolveInitiatedBy() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null
        || authentication.getName() == null
        || authentication.getName().isBlank()) {
      return "system";
    }
    return authentication.getName();
  }

  private TransactionResponse mapToResponse(Transaction t) {
    return TransactionResponse.builder()
        .id(t.getId())
        .transactionReference(t.getTransactionReference())
        .sourceAccountId(t.getSourceAccountId())
        .destinationAccountId(t.getDestinationAccountId())
        .transactionType(t.getTransactionType().getCode())
        .amount(t.getAmount())
        .currencyCode(t.getCurrencyCode())
        .status(t.getStatus())
        .description(t.getDescription())
        .build();
  }
}
