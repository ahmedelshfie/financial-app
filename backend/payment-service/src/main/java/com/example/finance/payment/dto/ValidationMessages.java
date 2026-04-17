package com.example.finance.payment.dto;

public final class ValidationMessages {
  private ValidationMessages() {}

  public static final String SOURCE_ACCOUNT_REQUIRED = "Source account ID is required";
  public static final String DEST_ACCOUNT_REQUIRED = "Destination account ID is required";
  public static final String BENEFICIARY_REQUIRED = "Beneficiary ID is required";
  public static final String CUSTOMER_ID_REQUIRED = "Customer ID is required";
  public static final String NAME_REQUIRED = "Name is required";
  public static final String ACCOUNT_NUMBER_REQUIRED = "Account number is required";
  public static final String BANK_CODE_REQUIRED = "Bank code is required";
  public static final String PAYMENT_STATUS_REQUIRED = "Payment status is required";
  public static final String AMOUNT_REQUIRED = "Amount is required";
  public static final String AMOUNT_POSITIVE = "Amount must be positive";
}
