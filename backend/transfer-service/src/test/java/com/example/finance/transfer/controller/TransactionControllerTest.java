package com.example.finance.transfer.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.finance.transfer.dto.TransactionResponse;
import com.example.finance.transfer.dto.TransferRequest;
import com.example.finance.transfer.service.TransactionService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

  @Mock private TransactionService service;

  @InjectMocks private TransactionController controller;

  @Test
  void listTransfers_delegatesToService() {
    TransactionResponse response =
        TransactionResponse.builder().transactionReference("TRF-1").build();
    when(service.listAll()).thenReturn(List.of(response));

    controller.listTransfers();

    verify(service).listAll();
  }

  @Test
  void transfer_requiresBearerPrefix() {
    TransferRequest request = new TransferRequest(1L, 2L, BigDecimal.TEN);

    IllegalArgumentException ex =
        assertThrows(
            IllegalArgumentException.class, () -> controller.transfer(request, "token-only"));

    assertEquals("Authorization header must use Bearer token", ex.getMessage());
  }

  @Test
  void transfer_stripsBearerPrefixBeforeDelegating() {
    TransferRequest request = new TransferRequest(1L, 2L, BigDecimal.TEN);
    TransactionResponse response =
        TransactionResponse.builder().transactionReference("TXN-1").build();
    when(service.transfer(eq(request), eq("abc123"))).thenReturn(response);

    controller.transfer(request, "Bearer abc123");

    verify(service).transfer(request, "abc123");
  }
}
