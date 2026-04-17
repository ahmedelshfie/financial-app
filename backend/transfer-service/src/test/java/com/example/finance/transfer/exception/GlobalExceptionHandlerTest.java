package com.example.finance.transfer.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

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

  @SuppressWarnings("null")
  @Test
  void handleMethodNotSupported_returnsMethodNotAllowed() {
    HttpRequestMethodNotSupportedException ex =
        new HttpRequestMethodNotSupportedException("PUT");
    MockHttpServletRequest servletRequest = new MockHttpServletRequest();
    servletRequest.setMethod("PUT");
    servletRequest.setRequestURI("/api/transfers");

    ResponseEntity<ErrorResponse> response = exceptionHandler.handleMethodNotSupported(ex, servletRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("METHOD_NOT_ALLOWED", response.getBody().errorCode());
    assertEquals("BAD_REQUEST", response.getBody().category());
    assertEquals("PUT", response.getBody().details().get("method"));
  }

  @SuppressWarnings("null")
  @Test
  void handleMediaTypeNotSupported_returnsUnsupportedMediaType() {
    HttpMediaTypeNotSupportedException ex =
        new HttpMediaTypeNotSupportedException(
            MediaType.TEXT_PLAIN, List.of(MediaType.APPLICATION_JSON));
    MockHttpServletRequest servletRequest = new MockHttpServletRequest();
    servletRequest.setRequestURI("/api/transfers");
    servletRequest.setContentType(MediaType.TEXT_PLAIN_VALUE);

    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleMediaTypeNotSupported(ex, servletRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("UNSUPPORTED_MEDIA_TYPE", response.getBody().errorCode());
    assertEquals("BAD_REQUEST", response.getBody().category());
    assertEquals(MediaType.TEXT_PLAIN_VALUE, response.getBody().details().get("contentType"));
    assertTrue(
        response
            .getBody()
            .details()
            .get("supportedMediaTypes")
            .contains(MediaType.APPLICATION_JSON_VALUE));
  }
}
