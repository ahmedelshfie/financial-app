package com.example.finance.report.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.example.finance.report.client.AccountClient;
import com.example.finance.report.client.HistoryClient;
import com.example.finance.report.dto.AccountResponse;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {
  @Mock AccountClient accountClient;
  @Mock HistoryClient historyClient;
  @InjectMocks ReportServiceImpl service;

  @Test
  void listReports_returnsDefaultCatalog() {
    var reports = service.listReports();
    assertEquals(3, reports.size());
    assertEquals("RPT-001", reports.get(0).getId());
    assertEquals("Monthly Liquidity Summary", reports.get(0).getName());
  }

  @Test
  void customerSummary_aggregatesAcrossAccounts() {
    AccountResponse a = new AccountResponse();
    a.setId(1L);
    a.setCurrentBalance(new BigDecimal("100.00"));
    AccountResponse b = new AccountResponse();
    b.setId(2L);
    b.setCurrentBalance(new BigDecimal("30.00"));
    when(accountClient.customerAccounts(10L, "t")).thenReturn(List.of(a, b));
    when(historyClient.transactions(1L, "t")).thenReturn(2);
    when(historyClient.transactions(2L, "t")).thenReturn(3);
    when(historyClient.transfers(1L, "t")).thenReturn(1);
    when(historyClient.transfers(2L, "t")).thenReturn(1);
    when(historyClient.payments(1L, "t")).thenReturn(4);
    when(historyClient.payments(2L, "t")).thenReturn(2);

    var summary = service.customerSummary(10L, "t");
    assertEquals(2, summary.getAccountsCount());
    assertEquals(5, summary.getTransactionsCount());
    assertEquals(2, summary.getTransfersCount());
    assertEquals(6, summary.getPaymentsCount());
    assertEquals(new BigDecimal("130.00"), summary.getTotalBalance());
  }
}
