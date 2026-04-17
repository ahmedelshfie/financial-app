package com.example.finance.report.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportListItemResponse {
  private String id;
  private String name;
  private String category;
  private String lastGeneratedAt;
  private String status;
}
