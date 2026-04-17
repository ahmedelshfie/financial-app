package com.example.finance.report.service;

import com.example.finance.report.dto.*;
import java.util.List;

public interface TransactionService {
  TransactionResponse transfer(TransferRequest request, String token);

  List<TransactionResponse> history(Long accountId);
}
