package com.example.finance.payment.service;

import com.example.finance.payment.dto.*;
import java.util.List;

public interface TransactionService {
  TransactionResponse transfer(TransferRequest request, String token);

  List<TransactionResponse> history(Long accountId);
}
