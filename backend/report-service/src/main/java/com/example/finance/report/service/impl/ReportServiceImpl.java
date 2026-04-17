package com.example.finance.report.service.impl;

import com.example.finance.report.client.AccountClient;
import com.example.finance.report.client.HistoryClient;
import com.example.finance.report.dto.AccountResponse;
import com.example.finance.report.dto.ReportListItemResponse;
import com.example.finance.report.dto.ReportSummaryResponse;
import com.example.finance.report.service.ReportService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl implements ReportService {
  private final AccountClient accountClient;
  private final HistoryClient historyClient;

  public ReportServiceImpl(AccountClient accountClient, HistoryClient historyClient) {
    this.accountClient = accountClient;
    this.historyClient = historyClient;
  }

  @Override
  public List<ReportListItemResponse> listReports() {
    return List.of(
        ReportListItemResponse.builder()
            .id("RPT-001")
            .name("Monthly Liquidity Summary")
            .category("Finance")
            .lastGeneratedAt("2026-04-15T23:00:00Z")
            .status("Ready")
            .build(),
        ReportListItemResponse.builder()
            .id("RPT-014")
            .name("Sanctions Screening Exceptions")
            .category("Compliance")
            .lastGeneratedAt("2026-04-16T02:05:00Z")
            .status("Running")
            .build(),
        ReportListItemResponse.builder()
            .id("RPT-021")
            .name("Failed Payments Drilldown")
            .category("Operations")
            .lastGeneratedAt("2026-04-15T21:45:00Z")
            .status("Ready")
            .build());
  }

  @Override
  public ReportSummaryResponse customerSummary(Long customerId, String token) {
    List<AccountResponse> accounts = accountClient.customerAccounts(customerId, token);
    BigDecimal total =
        accounts.stream()
            .map(AccountResponse::getCurrentBalance)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    int tx = 0, tr = 0, pay = 0;
    for (AccountResponse account : accounts) {
      tx += historyClient.transactions(account.getId(), token);
      tr += historyClient.transfers(account.getId(), token);
      pay += historyClient.payments(account.getId(), token);
    }
    return ReportSummaryResponse.builder()
        .customerId(customerId)
        .accountsCount(accounts.size())
        .transactionsCount(tx)
        .transfersCount(tr)
        .paymentsCount(pay)
        .totalBalance(total)
        .build();
  }
}
