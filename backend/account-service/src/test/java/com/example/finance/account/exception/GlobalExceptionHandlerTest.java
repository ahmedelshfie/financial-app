package com.example.finance.account.exception;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.finance.account.controller.AccountController;
import com.example.finance.account.security.JwtService;
import com.example.finance.account.service.AccountService;
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
 * Verifies {@link GlobalExceptionHandler} response shape and HTTP status codes using {@link
 * AccountController} as the test vehicle.
 *
 * <p>Spring Security is disabled so that tests concentrate on the exception-handler logic rather
 * than authentication.
 */
@WebMvcTest(
    controllers = AccountController.class,
    excludeAutoConfiguration = {
      org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
      org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
    })
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("GlobalExceptionHandler integration tests")
class GlobalExceptionHandlerTest {

  @Autowired private MockMvc mockMvc;

  @SuppressWarnings("removal")
  @MockBean
  private AccountService accountService;

  /** Satisfies the {@code JwtAuthenticationFilter} constructor resolved by {@code @WebMvcTest}. */
  @MockBean
  @SuppressWarnings({"unused", "removal"})
  private JwtService jwtService;

  // ─── AccountNotFoundException (404) ───────────────────────────────────────

  @Nested
  @DisplayName("AccountNotFoundException")
  class AccountNotFoundTests {

    @SuppressWarnings("null")
    @Test
    @DisplayName("returns 404 with message and timestamp")
    void accountNotFound_returns404WithBody() throws Exception {
      when(accountService.getById(anyLong())).thenThrow(new AccountNotFoundException(42L));

      mockMvc
          .perform(get("/api/accounts/42"))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("Account not found with id: 42"))
          .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }
  }

  // ─── InsufficientFundsException (422) ─────────────────────────────────────

  @Nested
  @DisplayName("InsufficientFundsException")
  class InsufficientFundsTests {

    @SuppressWarnings("null")
    @Test
    @DisplayName("returns 422 with message and timestamp")
    void insufficientFunds_returns422WithBody() throws Exception {
      when(accountService.updateBalance(anyLong(), any()))
          .thenThrow(new InsufficientFundsException());

      mockMvc
          .perform(
              patch("/api/accounts/1/balance")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{\"amount\":9999,\"operation\":\"DEBIT\"}"))
          .andExpect(status().isUnprocessableEntity())
          .andExpect(
              jsonPath("$.message")
                  .value("Insufficient funds: the debit amount exceeds the available balance"))
          .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }
  }

  // ─── IllegalArgumentException (400) ───────────────────────────────────────

  @Nested
  @DisplayName("IllegalArgumentException")
  class IllegalArgumentTests {

    @SuppressWarnings("null")
    @Test
    @DisplayName("returns 400 with message and timestamp")
    void illegalArgument_returns400WithBody() throws Exception {
      when(accountService.getById(anyLong()))
          .thenThrow(new IllegalArgumentException("Account type not found: UNKNOWN"));

      mockMvc
          .perform(get("/api/accounts/999"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message").value("Account type not found: UNKNOWN"))
          .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }
  }

  // ─── MethodArgumentNotValidException (400) ────────────────────────────────

  @Nested
  @DisplayName("MethodArgumentNotValidException")
  class ValidationTests {

    @SuppressWarnings("null")
    @Test
    @DisplayName("empty body returns 400 with field error in message")
    void validationFailed_returns400WithFieldError() throws Exception {
      mockMvc
          .perform(post("/api/accounts").contentType(MediaType.APPLICATION_JSON).content("{}"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message").value(notNullValue()))
          .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("message is a non-empty string describing the offending field")
    void validationFailed_messageContainsFieldAndConstraint() throws Exception {
      mockMvc
          .perform(post("/api/accounts").contentType(MediaType.APPLICATION_JSON).content("{}"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message").isString());
    }
  }

  // ─── HttpMessageNotReadableException (400) ────────────────────────────────

  @Nested
  @DisplayName("HttpMessageNotReadableException")
  class NotReadableTests {

    @SuppressWarnings("null")
    @Test
    @DisplayName("invalid enum value in operation field returns 400")
    void invalidEnumValue_returns400() throws Exception {
      mockMvc
          .perform(
              patch("/api/accounts/1/balance")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{\"amount\":10,\"operation\":\"TRANSFER\"}"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message").value(notNullValue()))
          .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }
  }
}
