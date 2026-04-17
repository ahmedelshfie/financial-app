package com.example.finance.dashboard.dto;

public final class ValidationMessages {
  private ValidationMessages() {}

  public static final String CUSTOMER_ID_POSITIVE = "Customer id must be positive";
  public static final String LIMIT_BETWEEN_1_AND_50 = "Recent-item limit must be between 1 and 50";
  public static final String TREND_DAYS_BETWEEN_3_AND_90 =
      "Trend days must be between 3 and 90";
}
