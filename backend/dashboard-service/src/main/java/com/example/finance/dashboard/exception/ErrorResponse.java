package com.example.finance.dashboard.exception;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import org.springframework.http.HttpStatus;

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
      HttpStatus status,
      String message,
      String path,
      String errorCode,
      String category,
      String traceId,
      Map<String, String> details) {
    return new ErrorResponse(
        OffsetDateTime.now(ZoneOffset.UTC),
        status.value(),
        status.getReasonPhrase(),
        message,
        path,
        errorCode,
        category,
        traceId,
        details);
  }
}
