package com.example.finance.customer.controller;

import com.example.finance.customer.dto.CreateCustomerRequest;
import com.example.finance.customer.dto.CustomerResponse;
import com.example.finance.customer.service.CustomerService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing customer resources.
 *
 * <p>Provides endpoints for creating, retrieving, and listing customers with proper validation and
 * error handling.
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

  private final CustomerService service;

  /**
   * Creates a new customer with the provided information.
   *
   * @param request the customer creation request containing validated data
   * @return the created customer response
   */
  @PostMapping
  public ResponseEntity<CustomerResponse> create(
      @Valid @RequestBody CreateCustomerRequest request) {
    return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
        .body(service.create(request));
  }

  /**
   * Retrieves a customer by their unique identifier.
   *
   * @param id the customer's unique identifier
   * @return the customer response
   */
  @GetMapping("/{id}")
  public ResponseEntity<CustomerResponse> getById(@PathVariable Long id) {
    return ResponseEntity.ok(service.getById(id));
  }

  /**
   * Retrieves all customers in the system.
   *
   * @return a list of all customer responses
   */
  @GetMapping
  public ResponseEntity<List<CustomerResponse>> findAll() {
    return ResponseEntity.ok(service.findAll());
  }
}
