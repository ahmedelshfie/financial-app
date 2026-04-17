package com.example.finance.transfer.dto;

public final class ValidationMessages {
  private ValidationMessages() {}

  public static final String SOURCE_ACCOUNT_REQUIRED = "Source account ID is required";
  public static final String DEST_ACCOUNT_REQUIRED = "Destination account ID is required";
  public static final String AMOUNT_REQUIRED = "Amount is required";
  public static final String AMOUNT_POSITIVE = "Amount must be positive";
}
