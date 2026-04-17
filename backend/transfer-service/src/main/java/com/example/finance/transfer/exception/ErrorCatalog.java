package com.example.finance.transfer.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCatalog {
  VALIDATION_ERROR(
      "VALIDATION_ERROR",
      "Some fields are invalid. Please review and try again.",
      HttpStatus.BAD_REQUEST,
      "VALIDATION"),
  CONSTRAINT_VIOLATION(
      "VALIDATION_ERROR",
      "Some request values are invalid. Please review and try again.",
      HttpStatus.BAD_REQUEST,
      "VALIDATION"),
  INVALID_REQUEST_BODY(
      "INVALID_REQUEST_BODY",
      "Request body is malformed or contains unsupported values.",
      HttpStatus.BAD_REQUEST,
      "VALIDATION"),
  BAD_REQUEST("BAD_REQUEST", "The request is invalid.", HttpStatus.BAD_REQUEST, "VALIDATION"),
  UNAUTHORIZED(
      "UNAUTHORIZED",
      "Authentication is required to access this resource.",
      HttpStatus.UNAUTHORIZED,
      "AUTHENTICATION"),
  FORBIDDEN(
      "FORBIDDEN",
      "You do not have permission to perform this action.",
      HttpStatus.FORBIDDEN,
      "AUTHORIZATION"),
  METHOD_NOT_ALLOWED(
      "METHOD_NOT_ALLOWED",
      "HTTP method is not supported for this endpoint.",
      HttpStatus.METHOD_NOT_ALLOWED,
      "BAD_REQUEST"),
  UNSUPPORTED_MEDIA_TYPE(
      "UNSUPPORTED_MEDIA_TYPE",
      "Content-Type is not supported for this endpoint.",
      HttpStatus.UNSUPPORTED_MEDIA_TYPE,
      "BAD_REQUEST"),
  UNSUPPORTED_OPERATION(
      "UNSUPPORTED_OPERATION",
      "This request is not supported by the API.",
      HttpStatus.BAD_REQUEST,
      "BAD_REQUEST"),
  DATA_CONFLICT(
      "DUPLICATE_OR_CONFLICT",
      "The request conflicts with existing data.",
      HttpStatus.CONFLICT,
      "CONFLICT"),
  INTERNAL_SERVER_ERROR(
      "INTERNAL_SERVER_ERROR",
      "An unexpected server error occurred. Please try again later.",
      HttpStatus.INTERNAL_SERVER_ERROR,
      "SERVER_FAILURE"),
  SAME_ACCOUNT_TRANSFER(
      "SAME_ACCOUNT_TRANSFER",
      "Source and destination accounts must be different",
      HttpStatus.BAD_REQUEST,
      "VALIDATION"),
  ACCOUNT_NOT_FOUND(
      "ACCOUNT_NOT_FOUND", "Account not found: %s", HttpStatus.BAD_REQUEST, "MISSING_DATA"),
  ACCOUNT_NOT_FOUND_GENERIC(
      "ACCOUNT_NOT_FOUND", "Account not found.", HttpStatus.BAD_REQUEST, "MISSING_DATA"),
  UNSUPPORTED_CURRENCY_TRANSFER(
      "UNSUPPORTED_CURRENCY_TRANSFER",
      "Cross-currency transfer not supported in starter version",
      HttpStatus.BAD_REQUEST,
      "BUSINESS_RULE"),
  TRANSACTION_TYPE_NOT_FOUND(
      "TRANSACTION_TYPE_NOT_FOUND",
      "Transaction type '%s' not found",
      HttpStatus.INTERNAL_SERVER_ERROR,
      "SERVER_FAILURE"),
  AUTH_HEADER_INVALID(
      "AUTH_HEADER_INVALID",
      "Authorization header must use Bearer token",
      HttpStatus.BAD_REQUEST,
      "VALIDATION"),
  AUTH_TOKEN_EMPTY(
      "AUTH_TOKEN_EMPTY", "Bearer token must not be empty.", HttpStatus.BAD_REQUEST, "VALIDATION");

  private final ErrorDefinition definition;

  ErrorCatalog(String code, String message, HttpStatus status, String category) {
    this.definition = new ErrorDefinition(code, message, status, category);
  }

  public ErrorDefinition definition() {
    return definition;
  }

  public String message(Object... args) {
    return args.length == 0 ? definition.message() : String.format(definition.message(), args);
  }
}
