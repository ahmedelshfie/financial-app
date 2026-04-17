package com.example.finance.transfer.service;

import com.example.finance.transfer.dto.TransactionResponse;
import com.example.finance.transfer.dto.TransferRequest;
import com.example.finance.transfer.dto.TransferStatusUpdateRequest;
import com.example.finance.transfer.dto.TransferValidationResponse;
import java.util.List;

public interface TransactionService {
  TransactionResponse transfer(TransferRequest request, String token);

  TransferValidationResponse validate(TransferRequest request, String token);

  List<TransactionResponse> listAll();

  List<TransactionResponse> history(Long accountId);

  TransactionResponse getByReference(String reference);

  TransactionResponse updateStatus(String reference, TransferStatusUpdateRequest request);
}
