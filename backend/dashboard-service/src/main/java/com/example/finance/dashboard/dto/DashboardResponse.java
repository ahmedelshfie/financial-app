package com.example.finance.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponse {
  private CustomerSnapshot customer;
  private SummaryCards summaryCards;
  private AccountSummary accountSummary;
  private CustomerTotals customerTotals;
  private QuickMetrics quickMetrics;
  private List<ActivityItem> recentTransactions;
  private List<ActivityItem> recentTransfers;
  private List<ActivityItem> recentPayments;
  private List<AlertItem> alerts;
  private List<ChartPoint> trend;

  @Data
  @Builder
  public static class CustomerSnapshot {
    private Long id;
    private String customerCode;
    private String fullName;
    private String status;
    private String kycStatus;
  }

  @Data
  @Builder
  public static class SummaryCards {
    private BigDecimal totalBalance;
    private BigDecimal availableBalance;
    private Integer totalAccounts;
    private Integer activeAccounts;
  }

  @Data
  @Builder
  public static class AccountSummary {
    private Integer checkingAccounts;
    private Integer savingsAccounts;
    private Integer loanAccounts;
    private Integer frozenAccounts;
  }

  @Data
  @Builder
  public static class CustomerTotals {
    private Integer allCustomers;
    private Integer activeCustomers;
    private Integer verifiedCustomers;
  }

  @Data
  @Builder
  public static class QuickMetrics {
    private Integer totalTransactions;
    private Integer totalTransfers;
    private Integer totalPayments;
    private Integer failedPayments;
    private Integer pendingTransfers;
  }

  @Data
  @Builder
  public static class ActivityItem {
    private String reference;
    private String type;
    private BigDecimal amount;
    private String currencyCode;
    private String status;
    private String description;
    private Long sourceAccountId;
    private Long destinationAccountId;
  }

  @Data
  @Builder
  public static class AlertItem {
    private String code;
    private String severity;
    private String message;
  }

  @Data
  @Builder
  public static class ChartPoint {
    private String label;
    private BigDecimal value;
  }
}
