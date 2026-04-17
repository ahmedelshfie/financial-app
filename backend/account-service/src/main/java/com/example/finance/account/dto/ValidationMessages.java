package com.example.finance.account.dto;

public final class ValidationMessages {
  private ValidationMessages() {}

  public static final String CUSTOMER_ID_REQUIRED = "customerId is required";
  public static final String CUSTOMER_ID_POSITIVE = "customerId must be positive";
  public static final String ACCOUNT_TYPE_REQUIRED = "accountTypeCode is required";
  public static final String ACCOUNT_TYPE_FORMAT = "accountTypeCode must be alphabetic";
  public static final String CURRENCY_REQUIRED = "currencyCode is required";
  public static final String CURRENCY_FORMAT = "currencyCode must be a 3-letter ISO code";
  public static final String AMOUNT_REQUIRED = "amount is required";
  public static final String AMOUNT_POSITIVE = "amount must be greater than zero";
  public static final String OPERATION_REQUIRED = "operation is required";
  public static final String ID_POSITIVE = "id must be positive";
}
