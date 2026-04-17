package com.example.finance.customer.service;

import com.example.finance.customer.dto.CreateCustomerRequest;
import com.example.finance.customer.dto.CustomerResponse;
import java.util.List;

/**
 * Service interface for customer-related business operations.
 *
 * <p>Defines the contract for creating, retrieving, and listing customers in the financial system.
 */
public interface CustomerService {

  /**
   * Creates a new customer with the provided information.
   *
   * @param request the customer creation request containing validated data
   * @return the created customer response
   * @throws IllegalArgumentException if email or national ID already exists
   */
  CustomerResponse create(CreateCustomerRequest request);

  /**
   * Retrieves a customer by their unique identifier.
   *
   * @param id the customer's unique identifier
   * @return the customer response
   * @throws IllegalArgumentException if customer not found
   */
  CustomerResponse getById(Long id);

  /**
   * Retrieves all customers in the system.
   *
   * @return a list of all customer responses
   */
  List<CustomerResponse> findAll();
}
