package com.example.finance.customer.exception;

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
    IllegalArgumentException ex = new IllegalArgumentException("Invalid customer data");
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/api/customers");

    ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgument(ex, request);

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid customer data", response.getBody().message());
    assertEquals("BAD_REQUEST", response.getBody().errorCode());
  }

  @SuppressWarnings("null")
  @Test
  void handleCustomerNotFound_returnsNotFoundCode() {
    CustomerNotFoundException ex = new CustomerNotFoundException(99L);
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/api/customers/99");

    ResponseEntity<ErrorResponse> response = exceptionHandler.handleCustomerNotFound(ex, request);

    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("CUSTOMER_NOT_FOUND", response.getBody().errorCode());
  }
}
