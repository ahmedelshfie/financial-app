package com.example.finance.dashboard.exception;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.finance.dashboard.controller.DashboardController;
import com.example.finance.dashboard.security.JwtService;
import com.example.finance.dashboard.service.DashboardService;
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
@DisplayName("GlobalExceptionHandler integration tests")
class GlobalExceptionHandlerTest {

  @Autowired private MockMvc mockMvc;

  @SuppressWarnings("removal")
  @MockBean
  private DashboardService dashboardService;

  @MockBean
  @SuppressWarnings({"unused", "removal"})
  private JwtService jwtService;

  @Test
  @DisplayName("customer not found returns 404 with standard payload")
  void customerNotFound_returns404() throws Exception {
    when(dashboardService.customerDashboard(anyLong(), anyString(), anyInt(), anyInt()))
        .thenThrow(new CustomerNotFoundException(99L));

    mockMvc
        .perform(
            get("/api/dashboard/customer/99")
                .header(HttpHeaders.AUTHORIZATION, "Bearer abc"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("CUSTOMER_NOT_FOUND"))
        .andExpect(jsonPath("$.timestamp", notNullValue()));
  }

  @Test
  @DisplayName("aggregation errors return 502")
  void aggregationError_returns502() throws Exception {
    when(dashboardService.customerDashboard(anyLong(), anyString(), anyInt(), anyInt()))
        .thenThrow(new DashboardAggregationException("failed", new RuntimeException()));

    mockMvc
        .perform(
            get("/api/dashboard/customer/99")
                .header(HttpHeaders.AUTHORIZATION, "Bearer abc"))
        .andExpect(status().isBadGateway())
        .andExpect(jsonPath("$.errorCode").value("DASHBOARD_AGGREGATION_FAILED"));
  }
}
