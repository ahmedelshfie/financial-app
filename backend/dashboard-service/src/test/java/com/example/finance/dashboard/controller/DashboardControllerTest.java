package com.example.finance.dashboard.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.finance.dashboard.dto.DashboardResponse;
import com.example.finance.dashboard.security.JwtService;
import com.example.finance.dashboard.service.DashboardService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = DashboardController.class,
    excludeAutoConfiguration = {
      org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
      org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
    })
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("DashboardController web-layer tests")
class DashboardControllerTest {

  @Autowired private MockMvc mockMvc;

  @SuppressWarnings("removal")
  @MockBean
  private DashboardService service;

  @MockBean
  @SuppressWarnings({"unused", "removal"})
  private JwtService jwtService;

  @Test
  @DisplayName("valid request returns dashboard payload")
  void customerDashboard_validRequest_returns200() throws Exception {
    DashboardResponse payload =
        DashboardResponse.builder()
            .summaryCards(
                DashboardResponse.SummaryCards.builder()
                    .totalBalance(new BigDecimal("2400.00"))
                    .totalAccounts(2)
                    .build())
            .alerts(
                List.of(
                    DashboardResponse.AlertItem.builder()
                        .code("LOW_BALANCE")
                        .severity("HIGH")
                        .message("1 account(s) below low-balance threshold")
                        .build()))
            .build();

    when(service.customerDashboard(eq(7L), eq("token-123"), eq(5), eq(30))).thenReturn(payload);

    mockMvc
        .perform(
            get("/api/dashboard/customer/7")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token-123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.summaryCards.totalBalance").value(2400.00))
        .andExpect(jsonPath("$.summaryCards.totalAccounts").value(2))
        .andExpect(jsonPath("$.alerts[0].code").value("LOW_BALANCE"));
  }

  @Test
  @DisplayName("invalid auth header returns 400")
  void customerDashboard_invalidHeader_returns400() throws Exception {
    mockMvc
        .perform(get("/api/dashboard/customer/7").header(HttpHeaders.AUTHORIZATION, "token-123"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"));
  }

  @Test
  @DisplayName("invalid limit query param returns 400")
  void customerDashboard_invalidLimit_returns400() throws Exception {
    mockMvc
        .perform(
            get("/api/dashboard/customer/7")
                .queryParam("limit", "0")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token-123"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
  }
}
