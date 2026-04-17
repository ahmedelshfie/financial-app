package com.example.finance.dashboard.exception;

public class CustomerNotFoundException extends RuntimeException {
  public CustomerNotFoundException(Long customerId) {
    super(ErrorCatalog.CUSTOMER_NOT_FOUND.message(customerId));
  }
}
