package com.example.finance.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BeneficiaryRequest {
  @NotNull(message = ValidationMessages.CUSTOMER_ID_REQUIRED)
  private Long customerId;

  @NotBlank(message = ValidationMessages.NAME_REQUIRED)
  private String name;

  @NotBlank(message = ValidationMessages.ACCOUNT_NUMBER_REQUIRED)
  private String accountNumber;

  @NotBlank(message = ValidationMessages.BANK_CODE_REQUIRED)
  private String bankCode;
}
