package com.example.finance.transfer.service.impl;

import com.example.finance.transfer.client.AccountClient;
import com.example.finance.transfer.dto.*;
import com.example.finance.transfer.entity.Transaction;
import com.example.finance.transfer.entity.TransactionType;
import com.example.finance.transfer.exception.ErrorCatalog;
import com.example.finance.transfer.repository.TransactionRepository;
import com.example.finance.transfer.repository.TransactionTypeRepository;
import com.example.finance.transfer.service.TransactionService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionServiceImpl implements TransactionService {

  private static final String TRANSACTION_REFERENCE_PREFIX = "TRF-";
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
    validateTransferRequest(request);
    AccountResponse source = fetchAccount(request.getSourceAccountId(), token);
    AccountResponse destination = fetchAccount(request.getDestinationAccountId(), token);
    validateCurrencyCompatibility(source, destination);
    updateAccountBalances(source, destination, request.getAmount(), token);

    TransactionType transferType = getTransferTransactionType();
    Transaction transaction =
        createAndSaveTransaction(source, destination, transferType, request.getAmount());
    return mapToResponse(transaction);
  }

  @Override
  public TransferValidationResponse validate(TransferRequest request, String token) {
    try {
      validateTransferRequest(request);
      AccountResponse source = fetchAccount(request.getSourceAccountId(), token);
      AccountResponse destination = fetchAccount(request.getDestinationAccountId(), token);
      validateCurrencyCompatibility(source, destination);
      return TransferValidationResponse.builder()
          .valid(true)
          .message("Transfer request is valid")
          .build();
    } catch (RuntimeException ex) {
      return TransferValidationResponse.builder().valid(false).message(ex.getMessage()).build();
    }
  }

  @Override
  public List<TransactionResponse> listAll() {
    return repository.findAllByOrderByCreatedAtDesc().stream().map(this::mapToResponse).toList();
  }

  @Override
  public List<TransactionResponse> history(Long accountId) {
    return repository.findBySourceAccountIdOrDestinationAccountId(accountId, accountId).stream()
        .map(this::mapToResponse)
        .toList();
  }

  @Override
  public TransactionResponse getByReference(String reference) {
    return repository
        .findByTransactionReference(reference)
        .map(this::mapToResponse)
        .orElseThrow(() -> new IllegalArgumentException(ErrorCatalog.BAD_REQUEST.message()));
  }

  @Override
  @Transactional
  public TransactionResponse updateStatus(String reference, TransferStatusUpdateRequest request) {
    Transaction transfer =
        repository
            .findByTransactionReference(reference)
            .orElseThrow(() -> new IllegalArgumentException(ErrorCatalog.BAD_REQUEST.message()));
    transfer.setStatus(request.getStatus().toUpperCase(Locale.ROOT));
    return mapToResponse(repository.save(transfer));
  }

  private void validateTransferRequest(TransferRequest request) {
    if (Objects.equals(request.getSourceAccountId(), request.getDestinationAccountId())) {
      throw new IllegalArgumentException(ErrorCatalog.SAME_ACCOUNT_TRANSFER.message());
    }
  }

  private AccountResponse fetchAccount(Long accountId, String token) {
    AccountResponse account = accountClient.getAccount(accountId, token);
    if (account == null) {
      throw new IllegalArgumentException(ErrorCatalog.ACCOUNT_NOT_FOUND.message(accountId));
    }
    return account;
  }

  private void validateCurrencyCompatibility(AccountResponse source, AccountResponse destination) {
    if (!source.getCurrencyCode().equals(destination.getCurrencyCode())) {
      throw new IllegalArgumentException(ErrorCatalog.UNSUPPORTED_CURRENCY_TRANSFER.message());
    }
  }

  private void updateAccountBalances(
      AccountResponse source, AccountResponse destination, BigDecimal amount, String token) {
    accountClient.updateBalance(source.getId(), new BalanceUpdateRequest(amount, "DEBIT"), token);
    accountClient.updateBalance(
        destination.getId(), new BalanceUpdateRequest(amount, "CREDIT"), token);
  }

  private TransactionType getTransferTransactionType() {
    return typeRepository
        .findByCode(TRANSFER_TYPE_CODE)
        .orElseThrow(
            () ->
                new IllegalStateException(
                    ErrorCatalog.TRANSACTION_TYPE_NOT_FOUND.message(TRANSFER_TYPE_CODE)));
  }

  @SuppressWarnings("null")
private Transaction createAndSaveTransaction(
      AccountResponse source,
      AccountResponse destination,
      TransactionType type,
      BigDecimal amount) {
    String initiatedBy = resolveInitiatedBy();
    Transaction transaction =
        Transaction.builder()
            .transactionReference(generateTransactionReference())
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
        + UUID.randomUUID().toString().substring(0, 12).toUpperCase(Locale.ROOT);
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
        // Some tests (and some DB reads) may not populate the transaction type relation.
        // Keep the mapping resilient and allow `transactionType` to be null in the DTO.
        .transactionType(t.getTransactionType() == null ? null : t.getTransactionType().getCode())
        .amount(t.getAmount())
        .currencyCode(t.getCurrencyCode())
        .status(t.getStatus())
        .description(t.getDescription())
        .createdAt(t.getCreatedAt())
        .build();
  }
}
