package com.example.finance.transfer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TransferStatusUpdateRequest {
  @NotBlank(message = "status is required")
  private String status;
}
