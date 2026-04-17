package com.example.finance.dashboard.dto;

import lombok.Data;

@Data
public class CustomerResponse {
  private Long id;
  private String customerCode;
  private String firstName;
  private String lastName;
  private String status;
  private String kycStatus;
}
