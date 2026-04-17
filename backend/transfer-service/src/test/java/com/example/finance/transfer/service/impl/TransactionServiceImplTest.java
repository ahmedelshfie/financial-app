package com.example.finance.transfer.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.finance.transfer.client.AccountClient;
import com.example.finance.transfer.dto.AccountResponse;
import com.example.finance.transfer.dto.BalanceUpdateRequest;
import com.example.finance.transfer.dto.TransactionResponse;
import com.example.finance.transfer.dto.TransferRequest;
import com.example.finance.transfer.entity.Transaction;
import com.example.finance.transfer.entity.TransactionType;
import com.example.finance.transfer.repository.TransactionRepository;
import com.example.finance.transfer.repository.TransactionTypeRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/** Unit tests for {@link TransactionServiceImpl}. */
@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

  @Mock private TransactionRepository repository;

  @Mock private TransactionTypeRepository typeRepository;

  @Mock private AccountClient accountClient;

  @Mock private Authentication authentication;

  @Mock private SecurityContext securityContext;

  @InjectMocks private TransactionServiceImpl service;

  private TransferRequest transferRequest;
  private AccountResponse sourceAccount;
  private AccountResponse destinationAccount;
  private TransactionType transactionType;
  private String authToken = "test-token";

  @BeforeEach
  void setUp() {
    transferRequest = new TransferRequest(1L, 2L, BigDecimal.valueOf(100.00));

    sourceAccount = new AccountResponse();
    sourceAccount.setId(1L);
    sourceAccount.setCurrencyCode("USD");

    destinationAccount = new AccountResponse();
    destinationAccount.setId(2L);
    destinationAccount.setCurrencyCode("USD");

    transactionType = new TransactionType();
    transactionType.setCode("TRANSFER");
    transactionType.setName("Transfer");

    lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
    lenient().when(authentication.getName()).thenReturn("test-user");
    SecurityContextHolder.setContext(securityContext);
  }

  @SuppressWarnings("null")
  @Test
  void testTransfer_Success() {
    // Arrange
    when(accountClient.getAccount(1L, authToken)).thenReturn(sourceAccount);
    when(accountClient.getAccount(2L, authToken)).thenReturn(destinationAccount);
    when(typeRepository.findByCode("TRANSFER")).thenReturn(Optional.of(transactionType));

    Transaction savedTransaction =
        Transaction.builder()
            .id(1L)
            .transactionReference("TXN-TEST123")
            .sourceAccountId(1L)
            .destinationAccountId(2L)
            .transactionType(transactionType)
            .amount(BigDecimal.valueOf(100.00))
            .currencyCode("USD")
            .status("COMPLETED")
            .description("Funds transfer")
            .initiatedBy("test-user")
            .build();

    when(repository.save(any(Transaction.class))).thenReturn(savedTransaction);

    // Act
    TransactionResponse response = service.transfer(transferRequest, authToken);

    // Assert
    assertNotNull(response);
    assertEquals("TXN-TEST123", response.getTransactionReference());
    assertEquals(BigDecimal.valueOf(100.00), response.getAmount());

    verify(accountClient).getAccount(1L, authToken);
    verify(accountClient).getAccount(2L, authToken);
    verify(accountClient).updateBalance(eq(1L), any(BalanceUpdateRequest.class), eq(authToken));
    verify(accountClient).updateBalance(eq(2L), any(BalanceUpdateRequest.class), eq(authToken));
    verify(typeRepository).findByCode("TRANSFER");
    verify(repository).save(any(Transaction.class));
  }

  @Test
  void testTransfer_SameAccount_ThrowsException() {
    // Arrange
    TransferRequest sameAccountRequest = new TransferRequest(1L, 1L, BigDecimal.valueOf(100.00));

    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> service.transfer(sameAccountRequest, authToken));

    assertEquals("Source and destination accounts must be different", exception.getMessage());
    verify(accountClient, never()).getAccount(anyLong(), anyString());
  }

  @Test
  void testTransfer_AccountNotFound_ThrowsException() {
    // Arrange
    when(accountClient.getAccount(1L, authToken)).thenReturn(null);

    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> service.transfer(transferRequest, authToken));

    assertTrue(exception.getMessage().contains("Account not found"));
  }

  @Test
  void testTransfer_CrossCurrency_ThrowsException() {
    // Arrange
    destinationAccount.setCurrencyCode("EUR");
    when(accountClient.getAccount(1L, authToken)).thenReturn(sourceAccount);
    when(accountClient.getAccount(2L, authToken)).thenReturn(destinationAccount);

    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> service.transfer(transferRequest, authToken));

    assertEquals(
        "Cross-currency transfer not supported in starter version", exception.getMessage());
  }

  @Test
  void testTransfer_TransactionTypeNotFound_ThrowsException() {
    // Arrange
    when(accountClient.getAccount(1L, authToken)).thenReturn(sourceAccount);
    when(accountClient.getAccount(2L, authToken)).thenReturn(destinationAccount);
    when(typeRepository.findByCode("TRANSFER")).thenReturn(Optional.empty());

    // Act & Assert
    IllegalStateException exception =
        assertThrows(
            IllegalStateException.class, () -> service.transfer(transferRequest, authToken));

    assertEquals("Transaction type 'TRANSFER' not found", exception.getMessage());
  }

  @SuppressWarnings("null")
  @Test
  void testTransfer_WithoutAuthenticationContext_defaultsInitiatedBySystem() {
    SecurityContextHolder.clearContext();

    when(accountClient.getAccount(1L, authToken)).thenReturn(sourceAccount);
    when(accountClient.getAccount(2L, authToken)).thenReturn(destinationAccount);
    when(typeRepository.findByCode("TRANSFER")).thenReturn(Optional.of(transactionType));
    when(repository.save(any(Transaction.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    TransactionResponse response = service.transfer(transferRequest, authToken);

    assertNotNull(response);
    verify(repository).save(argThat(transaction -> "system".equals(transaction.getInitiatedBy())));
  }

  @Test
  void testHistory_Success() {
    // Arrange
    Transaction transaction =
        Transaction.builder()
            .id(1L)
            .transactionReference("TXN-HIST123")
            .sourceAccountId(1L)
            .destinationAccountId(2L)
            .transactionType(transactionType)
            .amount(BigDecimal.valueOf(50.00))
            .currencyCode("USD")
            .status("COMPLETED")
            .description("Test transaction")
            .build();

    when(repository.findBySourceAccountIdOrDestinationAccountId(1L, 1L))
        .thenReturn(List.of(transaction));

    // Act
    List<TransactionResponse> result = service.history(1L);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("TXN-HIST123", result.get(0).getTransactionReference());

    verify(repository).findBySourceAccountIdOrDestinationAccountId(1L, 1L);
  }

  @Test
  void testListAll_Success() {
    Transaction transaction =
        Transaction.builder()
            .id(2L)
            .transactionReference("TRF-LIST1")
            .sourceAccountId(1L)
            .destinationAccountId(3L)
            .transactionType(transactionType)
            .amount(BigDecimal.valueOf(25.00))
            .currencyCode("USD")
            .status("COMPLETED")
            .description("List transaction")
            .build();

    when(repository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(transaction));

    List<TransactionResponse> result = service.listAll();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("TRF-LIST1", result.get(0).getTransactionReference());
    verify(repository).findAllByOrderByCreatedAtDesc();
  }
}
