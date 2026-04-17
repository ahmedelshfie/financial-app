package com.example.finance.auth.exception;

import org.springframework.http.HttpStatus;

public class AuthException extends RuntimeException {

  private final String errorCode;
  private final HttpStatus status;

  public AuthException(ErrorCatalog error) {
    super(error.message());
    this.errorCode = error.definition().code();
    this.status = error.definition().status();
  }

  public AuthException(String message, String errorCode, HttpStatus status) {
    super(message);
    this.errorCode = errorCode;
    this.status = status;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public HttpStatus getStatus() {
    return status;
  }
}
