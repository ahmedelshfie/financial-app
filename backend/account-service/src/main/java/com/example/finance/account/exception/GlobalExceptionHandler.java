package com.example.finance.account.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(AccountNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleAccountNotFound(
      AccountNotFoundException ex, HttpServletRequest request) {
    log.warn("Account not found: {}", ex.getMessage());
    return buildError(ex.getMessage(), ErrorCatalog.ACCOUNT_NOT_FOUND, null, request, false, ex);
  }

  @ExceptionHandler(InsufficientFundsException.class)
  public ResponseEntity<ErrorResponse> handleInsufficientFunds(
      InsufficientFundsException ex, HttpServletRequest request) {
    log.warn("Insufficient funds: {}", ex.getMessage());
    return buildError(ex.getMessage(), ErrorCatalog.INSUFFICIENT_FUNDS, null, request, false, ex);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    Map<String, String> details = new LinkedHashMap<>();
    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      details.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
    }
    return buildError(
        ErrorCatalog.VALIDATION_ERROR.message(),
        ErrorCatalog.VALIDATION_ERROR,
        details,
        request,
        false,
        ex);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(
      ConstraintViolationException ex, HttpServletRequest request) {
    Map<String, String> details = new LinkedHashMap<>();
    ex.getConstraintViolations()
        .forEach(v -> details.put(v.getPropertyPath().toString(), v.getMessage()));
    return buildError(
        ErrorCatalog.CONSTRAINT_VIOLATION.message(),
        ErrorCatalog.CONSTRAINT_VIOLATION,
        details,
        request,
        false,
        ex);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleNotReadable(
      HttpMessageNotReadableException ex, HttpServletRequest request) {
    return buildError(
        ErrorCatalog.INVALID_REQUEST_BODY.message(),
        ErrorCatalog.INVALID_REQUEST_BODY,
        null,
        request,
        false,
        ex);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(
      IllegalArgumentException ex, HttpServletRequest request) {
    return buildError(ex.getMessage(), ErrorCatalog.BAD_REQUEST, null, request, false, ex);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleAuthentication(
      AuthenticationException ex, HttpServletRequest request) {
    return buildError(
        ErrorCatalog.UNAUTHORIZED.message(), ErrorCatalog.UNAUTHORIZED, null, request, false, ex);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDenied(
      AccessDeniedException ex, HttpServletRequest request) {
    return buildError(
        ErrorCatalog.FORBIDDEN.message(), ErrorCatalog.FORBIDDEN, null, request, false, ex);
  }

  @ExceptionHandler({
    HttpRequestMethodNotSupportedException.class,
    HttpMediaTypeNotSupportedException.class
  })
  public ResponseEntity<ErrorResponse> handleUnsupportedOperation(
      Exception ex, HttpServletRequest request) {
    return buildError(
        ErrorCatalog.UNSUPPORTED_OPERATION.message(),
        ErrorCatalog.UNSUPPORTED_OPERATION,
        null,
        request,
        false,
        ex);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataConflict(
      DataIntegrityViolationException ex, HttpServletRequest request) {
    return buildError(
        ErrorCatalog.DATA_CONFLICT.message(), ErrorCatalog.DATA_CONFLICT, null, request, false, ex);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
    return buildError(
        ErrorCatalog.INTERNAL_SERVER_ERROR.message(),
        ErrorCatalog.INTERNAL_SERVER_ERROR,
        null,
        request,
        true,
        ex);
  }

  @SuppressWarnings("null")
  private ResponseEntity<ErrorResponse> buildError(
      String message,
      ErrorCatalog error,
      Map<String, String> details,
      HttpServletRequest request,
      boolean logStackTrace,
      Exception ex) {
    HttpStatus status = error.definition().status();
    String errorCode = error.definition().code();
    String category = error.definition().category();
    String traceId = resolveTraceId(request);
    if (logStackTrace) {
      log.error(
          "API error [{}] {} {} -> {}",
          traceId,
          request.getMethod(),
          request.getRequestURI(),
          errorCode,
          ex);
    } else {
      log.warn(
          "API error [{}] {} {} -> {} ({})",
          traceId,
          request.getMethod(),
          request.getRequestURI(),
          errorCode,
          ex.getMessage());
    }

    ErrorResponse response =
        ErrorResponse.of(
            status, message, request.getRequestURI(), errorCode, category, traceId, details);
    return ResponseEntity.status(status).body(response);
  }

  private String resolveTraceId(HttpServletRequest request) {
    String correlationId = request.getHeader("X-Correlation-Id");
    if (correlationId != null && !correlationId.isBlank()) {
      return correlationId;
    }
    return UUID.randomUUID().toString();
  }
}
