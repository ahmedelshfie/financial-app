package com.example.finance.customer.dto;

public final class ValidationMessages {
  private ValidationMessages() {}

  public static final String FIRST_NAME_REQUIRED = "First name is required";
  public static final String FIRST_NAME_MAX = "First name must not exceed 100 characters";
  public static final String LAST_NAME_REQUIRED = "Last name is required";
  public static final String LAST_NAME_MAX = "Last name must not exceed 100 characters";
  public static final String EMAIL_REQUIRED = "Email is required";
  public static final String EMAIL_VALID = "Email must be a valid email address";
  public static final String EMAIL_MAX = "Email must not exceed 255 characters";
  public static final String NATIONAL_ID_REQUIRED = "National ID is required";
  public static final String NATIONAL_ID_MAX = "National ID must not exceed 50 characters";
}
