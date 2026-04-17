package com.example.finance.dashboard.exception;

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
  CUSTOMER_NOT_FOUND(
      "CUSTOMER_NOT_FOUND", "Customer not found with id: %s", HttpStatus.NOT_FOUND, "MISSING_DATA"),
  AUTH_HEADER_INVALID(
      "AUTH_HEADER_INVALID",
      "Authorization header must use Bearer token",
      HttpStatus.BAD_REQUEST,
      "VALIDATION"),
  AUTH_TOKEN_EMPTY(
      "AUTH_TOKEN_EMPTY", "Bearer token must not be empty.", HttpStatus.BAD_REQUEST, "VALIDATION"),
  DASHBOARD_AGGREGATION_FAILED(
      "DASHBOARD_AGGREGATION_FAILED",
      "Failed to aggregate dashboard data from downstream services.",
      HttpStatus.BAD_GATEWAY,
      "UPSTREAM"),
  INTERNAL_SERVER_ERROR(
      "INTERNAL_SERVER_ERROR",
      "An unexpected server error occurred. Please try again later.",
      HttpStatus.INTERNAL_SERVER_ERROR,
      "SERVER_FAILURE");

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
