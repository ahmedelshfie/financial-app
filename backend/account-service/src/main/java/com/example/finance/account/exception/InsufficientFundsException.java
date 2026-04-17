package com.example.finance.account.exception;

public class InsufficientFundsException extends RuntimeException {

  public InsufficientFundsException() {
    super(ErrorCatalog.INSUFFICIENT_FUNDS.message());
  }
}
