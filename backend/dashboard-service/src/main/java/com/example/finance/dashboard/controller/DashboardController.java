package com.example.finance.dashboard.controller;

import com.example.finance.dashboard.dto.DashboardResponse;
import com.example.finance.dashboard.dto.ValidationMessages;
import com.example.finance.dashboard.exception.ErrorCatalog;
import com.example.finance.dashboard.service.DashboardService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/dashboard")
public class DashboardController {

  private static final String BEARER_PREFIX = "Bearer ";

  private final DashboardService service;

  public DashboardController(DashboardService service) {
    this.service = service;
  }

  @GetMapping
  public ResponseEntity<String> getDashboardBase() {
    return ResponseEntity.badRequest()
        .body("{\"error\": \"Customer ID is required. Please use /api/dashboard/customer/{customerId}\"}");
  }

  @GetMapping("/customer/{customerId}")
  public ResponseEntity<DashboardResponse> customerDashboard(
      @PathVariable @Positive(message = ValidationMessages.CUSTOMER_ID_POSITIVE) Long customerId,
      @RequestParam(defaultValue = "5") @Min(value = 1, message = ValidationMessages.LIMIT_BETWEEN_1_AND_50)
          @Max(value = 50, message = ValidationMessages.LIMIT_BETWEEN_1_AND_50)
          int limit,
      @RequestParam(defaultValue = "30")
          @Min(value = 3, message = ValidationMessages.TREND_DAYS_BETWEEN_3_AND_90)
          @Max(value = 90, message = ValidationMessages.TREND_DAYS_BETWEEN_3_AND_90)
          int trendDays,
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

    String token = extractBearerToken(authorization);
    return ResponseEntity.ok(service.customerDashboard(customerId, token, limit, trendDays));
  }

  private String extractBearerToken(String authorization) {
    if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
      throw new IllegalArgumentException(ErrorCatalog.AUTH_HEADER_INVALID.message());
    }

    String token = authorization.substring(BEARER_PREFIX.length()).trim();
    if (token.isEmpty()) {
      throw new IllegalArgumentException(ErrorCatalog.AUTH_TOKEN_EMPTY.message());
    }

    return token;
  }
}
