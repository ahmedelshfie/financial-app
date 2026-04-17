package com.example.finance.report.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportSummaryResponse {
  private Long customerId;
  private int accountsCount;
  private int transactionsCount;
  private int transfersCount;
  private int paymentsCount;
  private BigDecimal totalBalance;
}
