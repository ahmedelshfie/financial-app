package com.example.finance.dashboard.service.impl;

import com.example.finance.dashboard.client.AccountClient;
import com.example.finance.dashboard.client.ActivityClient;
import com.example.finance.dashboard.client.CustomerClient;
import com.example.finance.dashboard.dto.AccountResponse;
import com.example.finance.dashboard.dto.CustomerResponse;
import com.example.finance.dashboard.dto.DashboardResponse;
import com.example.finance.dashboard.dto.PaymentResponse;
import com.example.finance.dashboard.dto.TransactionResponse;
import com.example.finance.dashboard.exception.CustomerNotFoundException;
import com.example.finance.dashboard.exception.DashboardAggregationException;
import com.example.finance.dashboard.mapper.DashboardMapper;
import com.example.finance.dashboard.service.DashboardService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

@Service
public class DashboardServiceImpl implements DashboardService {

  private static final Logger log = LoggerFactory.getLogger(DashboardServiceImpl.class);

  private final CustomerClient customerClient;
  private final AccountClient accountClient;
  private final ActivityClient activityClient;
  private final DashboardMapper mapper;

  @Value("${app.dashboard.low-balance-threshold:100.00}")
  private BigDecimal lowBalanceThreshold;

  public DashboardServiceImpl(
      CustomerClient customerClient,
      AccountClient accountClient,
      ActivityClient activityClient,
      DashboardMapper mapper) {
    this.customerClient = customerClient;
    this.accountClient = accountClient;
    this.activityClient = activityClient;
    this.mapper = mapper;
  }

  @Override
  public DashboardResponse customerDashboard(Long customerId, String token, int limit, int trendDays) {
    try {
      CustomerResponse customer = customerClient.getById(customerId, token);
      if (customer == null) {
        throw new CustomerNotFoundException(customerId);
      }

      List<AccountResponse> accounts =
          safeListFetch(
              "accounts for customer " + customerId,
              () -> accountClient.customerAccounts(customerId, token));

      List<CustomerResponse> allCustomers =
          safeListFetch("all customers", () -> customerClient.findAll(token));
      if (allCustomers.isEmpty()) {
        allCustomers = List.of(customer);
      }

      List<TransactionResponse> allTransactions = new ArrayList<>();
      List<TransactionResponse> allTransfers = new ArrayList<>();
      List<PaymentResponse> allPayments = new ArrayList<>();

      for (AccountResponse account : accounts) {
        if (account == null || account.getId() == null) {
          continue;
        }

        Long accountId = account.getId();
        allTransactions.addAll(
            safeListFetch(
                "transactions for account " + accountId,
                () -> activityClient.transactions(accountId, token)));
        allTransfers.addAll(
            safeListFetch(
                "transfers for account " + accountId,
                () -> activityClient.transfers(accountId, token)));
        allPayments.addAll(
            safeListFetch(
                "payments for account " + accountId, () -> activityClient.payments(accountId, token)));
      }

      BigDecimal totalBalance =
          accounts.stream()
              .map(a -> a.getCurrentBalance() == null ? BigDecimal.ZERO : a.getCurrentBalance())
              .reduce(BigDecimal.ZERO, BigDecimal::add);

      BigDecimal availableBalance =
          accounts.stream()
              .map(a -> a.getAvailableBalance() == null ? BigDecimal.ZERO : a.getAvailableBalance())
              .reduce(BigDecimal.ZERO, BigDecimal::add);

      int activeAccounts = (int) accounts.stream().filter(a -> "ACTIVE".equalsIgnoreCase(a.getStatus())).count();
      int frozenAccounts = (int) accounts.stream().filter(a -> "FROZEN".equalsIgnoreCase(a.getStatus())).count();
      int checkingAccounts = (int) accounts.stream().filter(a -> "CHECKING".equalsIgnoreCase(a.getAccountTypeCode())).count();
      int savingsAccounts = (int) accounts.stream().filter(a -> "SAVINGS".equalsIgnoreCase(a.getAccountTypeCode())).count();
      int loanAccounts = (int) accounts.stream().filter(a -> "LOAN".equalsIgnoreCase(a.getAccountTypeCode())).count();

      int activeCustomers = (int) allCustomers.stream().filter(c -> "ACTIVE".equalsIgnoreCase(c.getStatus())).count();
      int verifiedCustomers = (int) allCustomers.stream().filter(c -> "VERIFIED".equalsIgnoreCase(c.getKycStatus())).count();

      int failedPayments = (int) allPayments.stream().filter(p -> "FAILED".equalsIgnoreCase(p.getStatus())).count();
      int pendingTransfers = (int) allTransfers.stream().filter(t -> "PENDING".equalsIgnoreCase(t.getStatus())).count();

      List<DashboardResponse.ActivityItem> recentTransactions =
          allTransactions.stream().map(mapper::fromTransaction).limit(limit).toList();
      List<DashboardResponse.ActivityItem> recentTransfers =
          allTransfers.stream().map(mapper::fromTransaction).limit(limit).toList();
      List<DashboardResponse.ActivityItem> recentPayments =
          allPayments.stream().map(mapper::fromPayment).limit(limit).toList();

      List<DashboardResponse.AlertItem> alerts = buildAlerts(customer, accounts, failedPayments);
      List<DashboardResponse.ChartPoint> trend = buildTrend(allTransactions, allTransfers, allPayments, trendDays);

      return DashboardResponse.builder()
          .customer(
              DashboardResponse.CustomerSnapshot.builder()
                  .id(customer.getId())
                  .customerCode(customer.getCustomerCode())
                  .fullName((customer.getFirstName() + " " + customer.getLastName()).trim())
                  .status(customer.getStatus())
                  .kycStatus(customer.getKycStatus())
                  .build())
          .summaryCards(
              DashboardResponse.SummaryCards.builder()
                  .totalBalance(totalBalance)
                  .availableBalance(availableBalance)
                  .totalAccounts(accounts.size())
                  .activeAccounts(activeAccounts)
                  .build())
          .accountSummary(
              DashboardResponse.AccountSummary.builder()
                  .checkingAccounts(checkingAccounts)
                  .savingsAccounts(savingsAccounts)
                  .loanAccounts(loanAccounts)
                  .frozenAccounts(frozenAccounts)
                  .build())
          .customerTotals(
              DashboardResponse.CustomerTotals.builder()
                  .allCustomers(allCustomers.size())
                  .activeCustomers(activeCustomers)
                  .verifiedCustomers(verifiedCustomers)
                  .build())
          .quickMetrics(
              DashboardResponse.QuickMetrics.builder()
                  .totalTransactions(allTransactions.size())
                  .totalTransfers(allTransfers.size())
                  .totalPayments(allPayments.size())
                  .failedPayments(failedPayments)
                  .pendingTransfers(pendingTransfers)
                  .build())
          .recentTransactions(recentTransactions)
          .recentTransfers(recentTransfers)
          .recentPayments(recentPayments)
          .alerts(alerts)
          .trend(trend)
          .build();

    } catch (HttpClientErrorException.NotFound ex) {
      log.warn("Customer {} not found", customerId);
      throw new CustomerNotFoundException(customerId);
    } catch (CustomerNotFoundException ex) {
      throw ex;
    } catch (RestClientException ex) {
      log.error("Dependent service unavailable while fetching dashboard for customer {}: {}", customerId, ex.getMessage(), ex);
      throw new DashboardAggregationException("A dependent service is currently unavailable. Please try again later.", ex);
    } catch (Exception ex) {
      log.error("Unexpected error while aggregating dashboard for customer {}: {}", customerId, ex.getMessage(), ex);
      throw new DashboardAggregationException("Failed to aggregate dashboard for customer " + customerId, ex);
    }
  }

  private <T> List<T> safeListFetch(String context, Supplier<List<T>> supplier) {
    try {
      List<T> result = supplier.get();
      return result == null ? List.of() : result;
    } catch (RestClientException ex) {
      log.warn("Skipping {} because downstream service call failed: {}", context, ex.getMessage());
      return List.of();
    }
  }

  private List<DashboardResponse.AlertItem> buildAlerts(
      CustomerResponse customer, List<AccountResponse> accounts, int failedPayments) {
    List<DashboardResponse.AlertItem> alerts = new ArrayList<>();

    if (!"VERIFIED".equalsIgnoreCase(customer.getKycStatus())) {
      alerts.add(
          DashboardResponse.AlertItem.builder()
              .code("KYC_PENDING")
              .severity("MEDIUM")
              .message("KYC verification is pending or incomplete")
              .build());
    }

    long lowBalanceAccounts =
        accounts.stream()
            .filter(a -> a.getAvailableBalance() != null)
            .filter(a -> a.getAvailableBalance().compareTo(lowBalanceThreshold) < 0)
            .count();
    if (lowBalanceAccounts > 0) {
      alerts.add(
          DashboardResponse.AlertItem.builder()
              .code("LOW_BALANCE")
              .severity("HIGH")
              .message(lowBalanceAccounts + " account(s) below low-balance threshold")
              .build());
    }

    if (failedPayments > 0) {
      alerts.add(
          DashboardResponse.AlertItem.builder()
              .code("FAILED_PAYMENTS")
              .severity("MEDIUM")
              .message(failedPayments + " failed payment(s) detected")
              .build());
    }

    return alerts;
  }

  private List<DashboardResponse.ChartPoint> buildTrend(
      List<TransactionResponse> transactions,
      List<TransactionResponse> transfers,
      List<PaymentResponse> payments,
      int trendDays) {
    int chunks = Math.max(1, Math.min(trendDays, 7));
    BigDecimal transactionsPerChunk =
        BigDecimal.valueOf(transactions.size()).divide(BigDecimal.valueOf(chunks), 2, RoundingMode.HALF_UP);
    BigDecimal transfersPerChunk =
        BigDecimal.valueOf(transfers.size()).divide(BigDecimal.valueOf(chunks), 2, RoundingMode.HALF_UP);
    BigDecimal paymentsPerChunk =
        BigDecimal.valueOf(payments.size()).divide(BigDecimal.valueOf(chunks), 2, RoundingMode.HALF_UP);

    List<DashboardResponse.ChartPoint> points = new ArrayList<>();
    for (int i = 1; i <= chunks; i++) {
      BigDecimal value = transactionsPerChunk.add(transfersPerChunk).add(paymentsPerChunk);
      points.add(DashboardResponse.ChartPoint.builder().label("T-" + (chunks - i)).value(value).build());
    }

    points.sort(Comparator.comparing(DashboardResponse.ChartPoint::getLabel));
    return points;
  }
}
