package com.example.finance.auth.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCatalog {
  USERNAME_TAKEN("USERNAME_TAKEN", "Username already exists", HttpStatus.CONFLICT, "VALIDATION"),
  EMAIL_TAKEN("EMAIL_TAKEN", "Email already exists", HttpStatus.CONFLICT, "VALIDATION"),
  ROLE_NOT_FOUND("ROLE_NOT_FOUND", "Role %s not found", HttpStatus.NOT_FOUND, "MISSING_DATA"),
  INVALID_CREDENTIALS(
      "INVALID_CREDENTIALS", "Invalid credentials", HttpStatus.UNAUTHORIZED, "AUTHENTICATION"),
  TOKEN_NOT_FOUND(
      "TOKEN_NOT_FOUND",
      "Refresh token was revoked or unrecognized",
      HttpStatus.UNAUTHORIZED,
      "AUTHENTICATION"),
  TOKEN_EXPIRED(
      "TOKEN_EXPIRED", "Refresh token was expired", HttpStatus.UNAUTHORIZED, "AUTHENTICATION"),
  USER_NOT_FOUND("USER_NOT_FOUND", "User not found", HttpStatus.UNAUTHORIZED, "AUTHENTICATION"),
  INVALID_TOKEN(
      "INVALID_TOKEN", "Invalid refresh token", HttpStatus.UNAUTHORIZED, "AUTHENTICATION"),
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
  INTERNAL_SERVER_ERROR(
      "INTERNAL_SERVER_ERROR",
      "An unexpected server error occurred. Please try again later.",
      HttpStatus.INTERNAL_SERVER_ERROR,
      "SERVER_FAILURE"),
  RESOURCE_NOT_FOUND(
      "RESOURCE_NOT_FOUND",
      "%s not found with identifier: %s",
      HttpStatus.NOT_FOUND,
      "MISSING_DATA");

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
