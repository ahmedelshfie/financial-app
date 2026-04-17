package com.example.finance.account.mapper;

import com.example.finance.account.dto.AccountResponse;
import com.example.finance.account.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

  public AccountResponse toResponse(Account account) {
    return AccountResponse.builder()
        .id(account.getId())
        .accountNumber(account.getAccountNumber())
        .customerId(account.getCustomerId())
        .accountTypeCode(account.getAccountType().getCode())
        .currencyCode(account.getCurrencyCode())
        .currentBalance(account.getCurrentBalance())
        .availableBalance(account.getAvailableBalance())
        .status(account.getStatus())
        .build();
  }
}
