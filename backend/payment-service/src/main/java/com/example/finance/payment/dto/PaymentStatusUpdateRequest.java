package com.example.finance.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentStatusUpdateRequest {
  @NotBlank(message = ValidationMessages.PAYMENT_STATUS_REQUIRED)
  private String status;
}
