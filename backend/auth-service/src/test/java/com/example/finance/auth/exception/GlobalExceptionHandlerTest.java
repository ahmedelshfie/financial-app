package com.example.finance.auth.exception;

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
  void testHandleAuthException() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/api/test");
    AuthException ex =
        new AuthException("Custom auth error", "AUTH_ERROR", HttpStatus.UNAUTHORIZED);

    ResponseEntity<ErrorResponse> responseEntity =
        exceptionHandler.handleAuthException(ex, request);

    assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
    assertEquals("Custom auth error", responseEntity.getBody().message());
    assertEquals(HttpStatus.UNAUTHORIZED.value(), responseEntity.getBody().status());
    assertEquals("/api/test", responseEntity.getBody().path());
  }

  @SuppressWarnings("null")
  @Test
  void testHandleResourceNotFoundException() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/api/test");
    ResourceNotFoundException ex = new ResourceNotFoundException("Not found error", "NOT_FOUND");

    ResponseEntity<ErrorResponse> responseEntity =
        exceptionHandler.handleResourceNotFoundException(ex, request);

    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
    assertEquals("Not found error", responseEntity.getBody().message());
    assertEquals(HttpStatus.NOT_FOUND.value(), responseEntity.getBody().status());
  }

  @SuppressWarnings("null")
  @Test
  void testHandleAllUncaughtException() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/api/test");
    Exception ex = new Exception("Unknown error");

    ResponseEntity<ErrorResponse> responseEntity =
        exceptionHandler.handleAllUncaughtException(ex, request);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseEntity.getBody().status());
  }
}
