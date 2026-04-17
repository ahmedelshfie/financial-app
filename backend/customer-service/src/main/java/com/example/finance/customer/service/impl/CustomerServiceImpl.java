package com.example.finance.customer.service.impl;

import com.example.finance.customer.dto.CreateCustomerRequest;
import com.example.finance.customer.dto.CustomerResponse;
import com.example.finance.customer.entity.Customer;
import com.example.finance.customer.entity.CustomerStatus;
import com.example.finance.customer.entity.KycStatus;
import com.example.finance.customer.exception.CustomerNotFoundException;
import com.example.finance.customer.exception.ErrorCatalog;
import com.example.finance.customer.repository.CustomerRepository;
import com.example.finance.customer.service.CustomerService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link CustomerService} providing business logic for customer operations.
 *
 * <p>Responsibilities:
 *
 * <ul>
 *   <li>Enforce uniqueness constraints on email and national ID before persisting.
 *   <li>Generate a unique customer code in the format {@code CUST-XXXXXXXX}.
 *   <li>Map {@link Customer} entities to {@link CustomerResponse} DTOs.
 * </ul>
 *
 * <p>All read operations are annotated with {@code @Transactional(readOnly = true)} for performance
 * optimization. Write operations override this with a full read-write transaction.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository repository;

  /** {@inheritDoc} */
  @Override
  @Transactional
  public CustomerResponse create(CreateCustomerRequest request) {
    log.debug("Attempting to create customer with email: {}", request.getEmail());

    if (repository.existsByEmail(request.getEmail())) {
      log.warn("Duplicate email rejected: {}", request.getEmail());
      throw new IllegalArgumentException(
          ErrorCatalog.EMAIL_ALREADY_IN_USE.message(request.getEmail()));
    }

    if (repository.existsByNationalId(request.getNationalId())) {
      log.warn("Duplicate national ID rejected: {}", request.getNationalId());
      throw new IllegalArgumentException(
          ErrorCatalog.NATIONAL_ID_ALREADY_IN_USE.message(request.getNationalId()));
    }

    String customerCode = generateCustomerCode();
    Customer customer =
        Customer.builder()
            .customerCode(customerCode)
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .nationalId(request.getNationalId())
            .status(CustomerStatus.ACTIVE.name())
            .kycStatus(KycStatus.PENDING.name())
            .build();

    @SuppressWarnings("null")
    Customer saved = repository.save(customer);
    log.info("Customer created: {} (code={})", saved.getId(), customerCode);
    return mapToResponse(saved);
  }

  /** {@inheritDoc} */
  @Override
  public CustomerResponse getById(Long id) {
    log.debug("Retrieving customer by ID: {}", id);
    @SuppressWarnings("null")
    Customer customer =
        repository
            .findById(id)
            .orElseThrow(
                () -> {
                  log.warn("Customer not found with ID: {}", id);
                  return new CustomerNotFoundException(id);
                });
    return mapToResponse(customer);
  }

  /** {@inheritDoc} */
  @Override
  public List<CustomerResponse> findAll() {
    log.debug("Retrieving all customers");
    return repository.findAll().stream().map(this::mapToResponse).toList();
  }

  // ─── Private helpers ──────────────────────────────────────────────────────

  /**
   * Generates a unique customer code in the format {@code CUST-XXXXXXXX}.
   *
   * <p>The suffix is derived from the first 8 characters of a random UUID converted to uppercase,
   * giving approximately 2.8 billion unique combinations — sufficient for customer volumes expected
   * in a banking starter.
   *
   * @return a new unique customer code
   */
  private String generateCustomerCode() {
    return "CUST-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
  }

  /**
   * Maps a {@link Customer} JPA entity to a {@link CustomerResponse} DTO.
   *
   * @param customer the entity to map
   * @return the corresponding response DTO
   */
  private CustomerResponse mapToResponse(Customer customer) {
    return CustomerResponse.builder()
        .id(customer.getId())
        .customerCode(customer.getCustomerCode())
        .firstName(customer.getFirstName())
        .lastName(customer.getLastName())
        .email(customer.getEmail())
        .nationalId(customer.getNationalId())
        .status(customer.getStatus())
        .kycStatus(customer.getKycStatus())
        .createdAt(customer.getCreatedAt())
        .build();
  }
}
