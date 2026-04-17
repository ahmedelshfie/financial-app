package com.example.finance.account.controller;

import com.example.finance.account.dto.AccountResponse;
import com.example.finance.account.dto.BalanceUpdateRequest;
import com.example.finance.account.dto.CreateAccountRequest;
import com.example.finance.account.dto.ValidationMessages;
import com.example.finance.account.service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing account management endpoints under {@code /api/accounts}.
 *
 * <p>All endpoints require a valid JWT bearer token (enforced by {@code JwtAuthenticationFilter}).
 * Validation errors and domain exceptions are translated to structured JSON responses by {@code
 * GlobalExceptionHandler}.
 *
 * <p>HTTP status semantics:
 *
 * <ul>
 *   <li>{@code 200 OK} — successful reads and updates
 *   <li>{@code 201 Created} — successful account creation
 *   <li>{@code 400 Bad Request} — validation failures or domain rule violations
 * </ul>
 */
@RestController
@Validated
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService service;

  /**
   * Creates a new bank account.
   *
   * @param request validated creation payload; must not be {@code null}
   * @return the newly created account details (HTTP 201)
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AccountResponse create(@Valid @RequestBody CreateAccountRequest request) {
    return service.create(request);
  }

  /**
   * Retrieves account details by its unique identifier.
   *
   * @param id the account's surrogate primary key
   * @return the account details (HTTP 200)
   */
  @GetMapping("/{id}")
  public AccountResponse getById(
      @PathVariable @Positive(message = ValidationMessages.ID_POSITIVE) Long id) {
    return service.getById(id);
  }

  /**
   * Returns all accounts owned by the specified customer.
   *
   * @param customerId the customer's unique identifier
   * @return a (possibly empty) list of account details (HTTP 200)
   */
  @GetMapping("/customer/{customerId}")
  public List<AccountResponse> byCustomer(
      @PathVariable @Positive(message = ValidationMessages.CUSTOMER_ID_POSITIVE) Long customerId) {
    return service.findByCustomerId(customerId);
  }

  /**
   * Applies a DEBIT or CREDIT operation to an account's balance.
   *
   * @param id the account's surrogate primary key
   * @param request validated balance update payload
   * @return the updated account details (HTTP 200)
   */
  @PatchMapping("/{id}/balance")
  public AccountResponse updateBalance(
      @PathVariable @Positive(message = ValidationMessages.ID_POSITIVE) Long id,
      @Valid @RequestBody BalanceUpdateRequest request) {
    return service.updateBalance(id, request);
  }
}
