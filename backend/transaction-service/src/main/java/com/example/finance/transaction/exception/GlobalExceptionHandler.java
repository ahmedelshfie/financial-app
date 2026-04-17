package com.example.finance.transaction.exception;

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

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    Map<String, String> details = new LinkedHashMap<>();
    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      details.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
    }

    return buildErrorResponse(
        ErrorCatalog.VALIDATION_ERROR.message(),
        ErrorCatalog.VALIDATION_ERROR,
        request,
        details,
        false,
        ex);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolationException(
      ConstraintViolationException ex, HttpServletRequest request) {
    Map<String, String> details = new LinkedHashMap<>();
    ex.getConstraintViolations()
        .forEach(v -> details.put(v.getPropertyPath().toString(), v.getMessage()));
    return buildErrorResponse(
        ErrorCatalog.CONSTRAINT_VIOLATION.message(),
        ErrorCatalog.CONSTRAINT_VIOLATION,
        request,
        details,
        false,
        ex);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleUnreadable(
      HttpMessageNotReadableException ex, HttpServletRequest request) {
    return buildErrorResponse(
        ErrorCatalog.INVALID_REQUEST_BODY.message(),
        ErrorCatalog.INVALID_REQUEST_BODY,
        request,
        null,
        false,
        ex);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException ex, HttpServletRequest request) {
    return buildErrorResponse(ex.getMessage(), ErrorCatalog.BAD_REQUEST, request, null, false, ex);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleAuthentication(
      AuthenticationException ex, HttpServletRequest request) {
    return buildErrorResponse(
        ErrorCatalog.UNAUTHORIZED.message(), ErrorCatalog.UNAUTHORIZED, request, null, false, ex);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDenied(
      AccessDeniedException ex, HttpServletRequest request) {
    return buildErrorResponse(
        ErrorCatalog.FORBIDDEN.message(), ErrorCatalog.FORBIDDEN, request, null, false, ex);
  }

  @ExceptionHandler({
    HttpRequestMethodNotSupportedException.class,
    HttpMediaTypeNotSupportedException.class
  })
  public ResponseEntity<ErrorResponse> handleUnsupportedOperation(
      Exception ex, HttpServletRequest request) {
    return buildErrorResponse(
        ErrorCatalog.UNSUPPORTED_OPERATION.message(),
        ErrorCatalog.UNSUPPORTED_OPERATION,
        request,
        null,
        false,
        ex);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataConflict(
      DataIntegrityViolationException ex, HttpServletRequest request) {
    return buildErrorResponse(
        ErrorCatalog.DATA_CONFLICT.message(), ErrorCatalog.DATA_CONFLICT, request, null, false, ex);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAllUncaughtException(
      Exception ex, HttpServletRequest request) {
    return buildErrorResponse(
        ErrorCatalog.INTERNAL_SERVER_ERROR.message(),
        ErrorCatalog.INTERNAL_SERVER_ERROR,
        request,
        null,
        true,
        ex);
  }

  @SuppressWarnings("null")
  private ResponseEntity<ErrorResponse> buildErrorResponse(
      String message,
      ErrorCatalog error,
      HttpServletRequest request,
      Map<String, String> details,
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
