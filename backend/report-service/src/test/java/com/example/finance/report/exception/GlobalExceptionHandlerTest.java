package com.example.finance.report.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

  @SuppressWarnings("null")
  @Test
  void handleIllegalArgumentException_returnsBadRequestWithMessage() {
    IllegalArgumentException ex = new IllegalArgumentException("Invalid transaction data");
    MockHttpServletRequest servletRequest = new MockHttpServletRequest();
    servletRequest.setRequestURI("/api/transactions/test");

    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleIllegalArgumentException(ex, servletRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Invalid transaction data", response.getBody().message());
    assertEquals("BAD_REQUEST", response.getBody().errorCode());
    assertEquals("VALIDATION", response.getBody().category());
  }

  @SuppressWarnings("null")
  @Test
  void handleAllUncaughtException_returnsInternalServerError() {
    Exception ex = new Exception("Unexpected error");
    MockHttpServletRequest servletRequest = new MockHttpServletRequest();
    servletRequest.setRequestURI("/api/transactions/test");

    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleAllUncaughtException(ex, servletRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("INTERNAL_SERVER_ERROR", response.getBody().errorCode());
    assertEquals("SERVER_FAILURE", response.getBody().category());
  }
}
