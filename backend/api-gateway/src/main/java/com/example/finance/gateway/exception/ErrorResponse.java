package com.example.finance.gateway.exception;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

/** Standardized API error payload for gateway errors. */
public record ErrorResponse(
    OffsetDateTime timestamp,
    int status,
    String error,
    String message,
    String path,
    String errorCode,
    String category,
    String traceId,
    Map<String, String> details) {

  public static ErrorResponse of(
      int status,
      String error,
      String message,
      String path,
      String errorCode,
      String category,
      String traceId,
      Map<String, String> details) {
    return new ErrorResponse(
        OffsetDateTime.now(ZoneOffset.UTC),
        status,
        error,
        message,
        path,
        errorCode,
        category,
        traceId,
        details);
  }
}
