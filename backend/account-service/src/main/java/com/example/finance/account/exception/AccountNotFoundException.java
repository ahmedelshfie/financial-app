package com.example.finance.account.exception;

public class AccountNotFoundException extends RuntimeException {

  public AccountNotFoundException(Long id) {
    super(String.format(ErrorCatalog.ACCOUNT_NOT_FOUND.message(), id));
  }
}
