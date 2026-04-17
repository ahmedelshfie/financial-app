package com.example.finance.account.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.finance.account.dto.AccountResponse;
import com.example.finance.account.dto.BalanceUpdateRequest;
import com.example.finance.account.dto.CreateAccountRequest;
import com.example.finance.account.dto.OperationType;
import com.example.finance.account.exception.AccountNotFoundException;
import com.example.finance.account.exception.InsufficientFundsException;
import com.example.finance.account.security.JwtService;
import com.example.finance.account.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Web-layer slice test for {@link AccountController}.
 *
 * <p>Spring Security auto-configuration is excluded so tests focus purely on controller routing and
 * request/response serialisation logic.
 */
@WebMvcTest(
    controllers = AccountController.class,
    excludeAutoConfiguration = {
      org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
      org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
    })
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AccountController web-layer tests")
class AccountControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @SuppressWarnings("removal")
  @MockBean
  private AccountService service;

  /**
   * Required because {@code JwtAuthenticationFilter} is a {@code @Component} picked up by
   * {@code @WebMvcTest}.
   */
  @MockBean
  @SuppressWarnings({"unused", "removal"})
  private JwtService jwtService;

  private AccountResponse sampleResponse;

  @BeforeEach
  void setUp() {
    sampleResponse =
        AccountResponse.builder()
            .id(1L)
            .accountNumber("ACC-ABCDE12345")
            .customerId(99L)
            .accountTypeCode("CHECKING")
            .currencyCode("USD")
            .currentBalance(BigDecimal.ZERO)
            .availableBalance(BigDecimal.ZERO)
            .status("ACTIVE")
            .build();
  }

  // ─── POST /api/accounts ───────────────────────────────────────────────────

  @Nested
  @DisplayName("POST /api/accounts")
  class CreateTests {

    @SuppressWarnings("null")
    @Test
    @DisplayName("valid request returns 201 with response body")
    void create_validRequest_returns201() throws Exception {
      CreateAccountRequest request = new CreateAccountRequest();
      request.setCustomerId(99L);
      request.setAccountTypeCode("CHECKING");
      request.setCurrencyCode("USD");

      when(service.create(any(CreateAccountRequest.class))).thenReturn(sampleResponse);

      mockMvc
          .perform(
              post("/api/accounts")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value(1))
          .andExpect(jsonPath("$.accountNumber").value("ACC-ABCDE12345"))
          .andExpect(jsonPath("$.customerId").value(99))
          .andExpect(jsonPath("$.accountTypeCode").value("CHECKING"))
          .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("missing required fields returns 400")
    void create_missingFields_returns400() throws Exception {
      mockMvc
          .perform(post("/api/accounts").contentType(MediaType.APPLICATION_JSON).content("{}"))
          .andExpect(status().isBadRequest());
    }
  }

  // ─── GET /api/accounts/{id} ───────────────────────────────────────────────

  @Nested
  @DisplayName("GET /api/accounts/{id}")
  class GetByIdTests {

    @Test
    @DisplayName("existing account returns 200 with body")
    void getById_exists_returns200() throws Exception {
      when(service.getById(1L)).thenReturn(sampleResponse);

      mockMvc
          .perform(get("/api/accounts/1"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(1))
          .andExpect(jsonPath("$.accountNumber").value("ACC-ABCDE12345"));
    }

    @Test
    @DisplayName("AccountNotFoundException returns 404 with message")
    void getById_notFound_returns404() throws Exception {
      when(service.getById(999L)).thenThrow(new AccountNotFoundException(999L));

      mockMvc
          .perform(get("/api/accounts/999"))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("Account not found with id: 999"))
          .andExpect(jsonPath("$.errorCode").value("ACCOUNT_NOT_FOUND"));
    }
  }

  // ─── GET /api/accounts/customer/{customerId} ──────────────────────────────

  @Nested
  @DisplayName("GET /api/accounts/customer/{customerId}")
  class ByCustomerTests {

    @Test
    @DisplayName("returns list of accounts with correct size")
    void byCustomer_returns200() throws Exception {
      when(service.findByCustomerId(99L)).thenReturn(List.of(sampleResponse));

      mockMvc
          .perform(get("/api/accounts/customer/99"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(1))
          .andExpect(jsonPath("$[0].customerId").value(99));
    }

    @Test
    @DisplayName("no accounts for customer returns 200 with empty array")
    void byCustomer_noAccounts_returns200EmptyArray() throws Exception {
      when(service.findByCustomerId(1L)).thenReturn(List.of());

      mockMvc
          .perform(get("/api/accounts/customer/1"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(0));
    }
  }

  // ─── PATCH /api/accounts/{id}/balance ────────────────────────────────────

  @Nested
  @DisplayName("PATCH /api/accounts/{id}/balance")
  class UpdateBalanceTests {

    @SuppressWarnings("null")
    @Test
    @DisplayName("valid CREDIT returns 200 with updated balance")
    void updateBalance_credit_returns200() throws Exception {
      AccountResponse credited =
          AccountResponse.builder()
              .id(1L)
              .accountNumber("ACC-ABCDE12345")
              .customerId(99L)
              .accountTypeCode("CHECKING")
              .currencyCode("USD")
              .currentBalance(new BigDecimal("150.00"))
              .availableBalance(new BigDecimal("150.00"))
              .status("ACTIVE")
              .build();

      BalanceUpdateRequest req = new BalanceUpdateRequest();
      req.setAmount(new BigDecimal("150.00"));
      req.setOperation(OperationType.CREDIT);

      when(service.updateBalance(eq(1L), any(BalanceUpdateRequest.class))).thenReturn(credited);

      mockMvc
          .perform(
              patch("/api/accounts/1/balance")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(req)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.currentBalance").value(150.00));
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("InsufficientFundsException returns 422 with message")
    void updateBalance_insufficientFunds_returns422() throws Exception {
      BalanceUpdateRequest req = new BalanceUpdateRequest();
      req.setAmount(new BigDecimal("9999.00"));
      req.setOperation(OperationType.DEBIT);

      when(service.updateBalance(eq(1L), any(BalanceUpdateRequest.class)))
          .thenThrow(new InsufficientFundsException());

      mockMvc
          .perform(
              patch("/api/accounts/1/balance")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(req)))
          .andExpect(status().isUnprocessableEntity())
          .andExpect(
              jsonPath("$.message")
                  .value("Insufficient funds: the debit amount exceeds the available balance"))
          .andExpect(jsonPath("$.errorCode").value("INSUFFICIENT_FUNDS"));
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("missing body fields returns 400")
    void updateBalance_missingFields_returns400() throws Exception {
      mockMvc
          .perform(
              patch("/api/accounts/1/balance")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{}"))
          .andExpect(status().isBadRequest());
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("invalid operation string returns 400")
    void updateBalance_invalidOperation_returns400() throws Exception {
      mockMvc
          .perform(
              patch("/api/accounts/1/balance")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{\"amount\":10,\"operation\":\"TRANSFER\"}"))
          .andExpect(status().isBadRequest());
    }
  }
}
