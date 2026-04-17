package com.example.finance.transfer.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferValidationResponse {
  private boolean valid;
  private String message;
}
