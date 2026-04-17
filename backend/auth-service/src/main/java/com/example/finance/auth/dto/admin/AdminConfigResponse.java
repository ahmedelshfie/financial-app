package com.example.finance.auth.dto.admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminConfigResponse {
  private String key;
  private String value;
}
