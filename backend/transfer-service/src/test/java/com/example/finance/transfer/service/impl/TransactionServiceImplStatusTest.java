package com.example.finance.transfer.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.example.finance.transfer.client.AccountClient;
import com.example.finance.transfer.dto.TransferStatusUpdateRequest;
import com.example.finance.transfer.entity.Transaction;
import com.example.finance.transfer.repository.TransactionRepository;
import com.example.finance.transfer.repository.TransactionTypeRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplStatusTest {
  @Mock TransactionRepository repository;
  @Mock TransactionTypeRepository typeRepository;
  @Mock AccountClient accountClient;
  @InjectMocks TransactionServiceImpl service;

  @SuppressWarnings("null")
  @Test
  void updateStatus_updatesTransferState() {
    Transaction t = Transaction.builder().transactionReference("TRF-1").status("PENDING").build();
    when(repository.findByTransactionReference("TRF-1")).thenReturn(Optional.of(t));
    when(repository.save(t)).thenReturn(t);

    TransferStatusUpdateRequest req = new TransferStatusUpdateRequest();
    req.setStatus("completed");
    var result = service.updateStatus("TRF-1", req);
    assertEquals("COMPLETED", result.getStatus());
  }
}
