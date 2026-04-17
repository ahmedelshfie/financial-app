package com.example.finance.auth.exception;

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

  @ExceptionHandler(AuthException.class)
  public ResponseEntity<ErrorResponse> handleAuthException(
      AuthException ex, HttpServletRequest request) {
    String category = ex.getStatus() == HttpStatus.FORBIDDEN ? "AUTHORIZATION" : "AUTHENTICATION";
    return buildError(
        ex.getStatus(), ex.getMessage(), request, ex.getErrorCode(), category, null, false, ex);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
      ResourceNotFoundException ex, HttpServletRequest request) {
    return buildError(
        HttpStatus.NOT_FOUND,
        ex.getMessage(),
        request,
        ex.getErrorCode(),
        ErrorCatalog.ROLE_NOT_FOUND.definition().category(),
        null,
        false,
        ex);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    Map<String, String> details = new LinkedHashMap<>();
    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      details.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
    }

    return buildError(
        ErrorCatalog.VALIDATION_ERROR.definition().status(),
        ErrorCatalog.VALIDATION_ERROR.message(),
        request,
        ErrorCatalog.VALIDATION_ERROR.definition().code(),
        ErrorCatalog.VALIDATION_ERROR.definition().category(),
        details,
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
        ErrorCatalog.CONSTRAINT_VIOLATION.definition().status(),
        ErrorCatalog.CONSTRAINT_VIOLATION.message(),
        request,
        ErrorCatalog.CONSTRAINT_VIOLATION.definition().code(),
        ErrorCatalog.CONSTRAINT_VIOLATION.definition().category(),
        details,
        false,
        ex);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleNotReadable(
      HttpMessageNotReadableException ex, HttpServletRequest request) {
    return buildError(
        ErrorCatalog.INVALID_REQUEST_BODY.definition().status(),
        ErrorCatalog.INVALID_REQUEST_BODY.message(),
        request,
        ErrorCatalog.INVALID_REQUEST_BODY.definition().code(),
        ErrorCatalog.INVALID_REQUEST_BODY.definition().category(),
        null,
        false,
        ex);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException ex, HttpServletRequest request) {
    return buildError(
        ErrorCatalog.BAD_REQUEST.definition().status(),
        ex.getMessage(),
        request,
        ErrorCatalog.BAD_REQUEST.definition().code(),
        ErrorCatalog.BAD_REQUEST.definition().category(),
        null,
        false,
        ex);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleAuthentication(
      AuthenticationException ex, HttpServletRequest request) {
    return buildError(
        ErrorCatalog.UNAUTHORIZED.definition().status(),
        ErrorCatalog.UNAUTHORIZED.message(),
        request,
        ErrorCatalog.UNAUTHORIZED.definition().code(),
        ErrorCatalog.UNAUTHORIZED.definition().category(),
        null,
        false,
        ex);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDenied(
      AccessDeniedException ex, HttpServletRequest request) {
    return buildError(
        ErrorCatalog.FORBIDDEN.definition().status(),
        ErrorCatalog.FORBIDDEN.message(),
        request,
        ErrorCatalog.FORBIDDEN.definition().code(),
        ErrorCatalog.FORBIDDEN.definition().category(),
        null,
        false,
        ex);
  }

  @ExceptionHandler({
    HttpRequestMethodNotSupportedException.class,
    HttpMediaTypeNotSupportedException.class
  })
  public ResponseEntity<ErrorResponse> handleUnsupportedOperation(
      Exception ex, HttpServletRequest request) {
    return buildError(
        ErrorCatalog.UNSUPPORTED_OPERATION.definition().status(),
        ErrorCatalog.UNSUPPORTED_OPERATION.message(),
        request,
        ErrorCatalog.UNSUPPORTED_OPERATION.definition().code(),
        ErrorCatalog.UNSUPPORTED_OPERATION.definition().category(),
        null,
        false,
        ex);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataConflict(
      DataIntegrityViolationException ex, HttpServletRequest request) {
    return buildError(
        ErrorCatalog.DATA_CONFLICT.definition().status(),
        ErrorCatalog.DATA_CONFLICT.message(),
        request,
        ErrorCatalog.DATA_CONFLICT.definition().code(),
        ErrorCatalog.DATA_CONFLICT.definition().category(),
        null,
        false,
        ex);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAllUncaughtException(
      Exception ex, HttpServletRequest request) {
    return buildError(
        ErrorCatalog.INTERNAL_SERVER_ERROR.definition().status(),
        ErrorCatalog.INTERNAL_SERVER_ERROR.message(),
        request,
        ErrorCatalog.INTERNAL_SERVER_ERROR.definition().code(),
        ErrorCatalog.INTERNAL_SERVER_ERROR.definition().category(),
        null,
        true,
        ex);
  }

  @SuppressWarnings("null")
  private ResponseEntity<ErrorResponse> buildError(
      HttpStatus status,
      String message,
      HttpServletRequest request,
      String errorCode,
      String category,
      Map<String, String> details,
      boolean logStackTrace,
      Exception ex) {
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

    return ResponseEntity.status(status)
        .body(
            ErrorResponse.of(
                status, message, request.getRequestURI(), errorCode, category, traceId, details));
  }

  private String resolveTraceId(HttpServletRequest request) {
    String correlationId = request.getHeader("X-Correlation-Id");
    if (correlationId != null && !correlationId.isBlank()) {
      return correlationId;
    }
    return UUID.randomUUID().toString();
  }
}
