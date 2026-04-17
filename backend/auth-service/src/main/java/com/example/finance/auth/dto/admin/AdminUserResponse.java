package com.example.finance.auth.dto.admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminUserResponse {
  private Long id;
  private String username;
  private String role;
  private String status;
}
