package com.example.finance.customer.exception;

public class CustomerNotFoundException extends RuntimeException {

  public CustomerNotFoundException(Long id) {
    super(ErrorCatalog.CUSTOMER_NOT_FOUND.message(id));
  }
}
