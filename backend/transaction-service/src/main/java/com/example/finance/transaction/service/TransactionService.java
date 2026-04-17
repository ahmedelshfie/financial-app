package com.example.finance.transaction.service;

import com.example.finance.transaction.dto.*;
import java.util.List;

public interface TransactionService {
  TransactionResponse transfer(TransferRequest request, String token);

  List<TransactionResponse> listAll();

  List<TransactionResponse> history(Long accountId);
}
