package com.example.finance.auth.dto;

public final class ValidationMessages {
  private ValidationMessages() {}

  public static final String USERNAME_REQUIRED = "Username cannot be blank";
  public static final String USERNAME_SIZE = "Username must be between 3 and 100 characters";
  public static final String EMAIL_REQUIRED = "Email cannot be blank";
  public static final String EMAIL_VALID = "Email must be valid";
  public static final String EMAIL_SIZE = "Email must not exceed 150 characters";
  public static final String PASSWORD_REQUIRED = "Password cannot be blank";
  public static final String PASSWORD_SIZE = "Password must be between 8 and 100 characters";
  public static final String PASSWORD_COMPLEXITY =
      "Password must contain at least one uppercase letter, one lowercase letter, one number, and"
          + " one special character";
  public static final String REFRESH_TOKEN_REQUIRED = "Refresh token cannot be blank";
}
