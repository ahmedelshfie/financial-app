package com.example.finance.dashboard.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

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
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;

@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardServiceImpl tests")
class DashboardServiceImplTest {

  @Mock private CustomerClient customerClient;
  @Mock private AccountClient accountClient;
  @Mock private ActivityClient activityClient;

  private DashboardServiceImpl service;

  @BeforeEach
  void setUp() {
    service =
        new DashboardServiceImpl(customerClient, accountClient, activityClient, new DashboardMapper());
    ReflectionTestUtils.setField(service, "lowBalanceThreshold", new BigDecimal("100.00"));
  }

  @Test
  @DisplayName("aggregates summary, metrics and alerts")
  void customerDashboard_aggregatesData() {
    CustomerResponse customer = new CustomerResponse();
    customer.setId(11L);
    customer.setCustomerCode("CUST-11");
    customer.setFirstName("Jane");
    customer.setLastName("Doe");
    customer.setStatus("ACTIVE");
    customer.setKycStatus("PENDING");

    AccountResponse a1 = new AccountResponse();
    a1.setId(100L);
    a1.setAccountTypeCode("CHECKING");
    a1.setCurrentBalance(new BigDecimal("80.00"));
    a1.setAvailableBalance(new BigDecimal("75.00"));
    a1.setStatus("ACTIVE");

    AccountResponse a2 = new AccountResponse();
    a2.setId(101L);
    a2.setAccountTypeCode("SAVINGS");
    a2.setCurrentBalance(new BigDecimal("920.00"));
    a2.setAvailableBalance(new BigDecimal("920.00"));
    a2.setStatus("FROZEN");

    TransactionResponse tx = new TransactionResponse();
    tx.setTransactionReference("TX-1");
    tx.setTransactionType("TRANSFER");
    tx.setAmount(new BigDecimal("150.00"));
    tx.setStatus("COMPLETED");

    PaymentResponse payment = new PaymentResponse();
    payment.setPaymentReference("PAY-1");
    payment.setAmount(new BigDecimal("45.00"));
    payment.setStatus("FAILED");

    when(customerClient.getById(11L, "token")).thenReturn(customer);
    when(customerClient.findAll("token")).thenReturn(List.of(customer));
    when(accountClient.customerAccounts(11L, "token")).thenReturn(List.of(a1, a2));
    when(activityClient.transactions(100L, "token")).thenReturn(List.of(tx));
    when(activityClient.transactions(101L, "token")).thenReturn(List.of());
    when(activityClient.transfers(100L, "token")).thenReturn(List.of(tx));
    when(activityClient.transfers(101L, "token")).thenReturn(List.of());
    when(activityClient.payments(100L, "token")).thenReturn(List.of(payment));
    when(activityClient.payments(101L, "token")).thenReturn(List.of());

    DashboardResponse response = service.customerDashboard(11L, "token", 5, 30);

    assertEquals(new BigDecimal("1000.00"), response.getSummaryCards().getTotalBalance());
    assertEquals(2, response.getSummaryCards().getTotalAccounts());
    assertEquals(1, response.getQuickMetrics().getFailedPayments());
    assertEquals(3, response.getAlerts().size());
    assertEquals(1, response.getRecentTransactions().size());
  }

  @Test
  @DisplayName("throws CustomerNotFoundException when downstream customer is missing")
  void customerDashboard_missingCustomer_throwsNotFound() {
    when(customerClient.getById(22L, "token")).thenReturn(null);

    assertThrows(
        CustomerNotFoundException.class, () -> service.customerDashboard(22L, "token", 5, 30));
  }

  @Test
  @DisplayName("wraps downstream errors with DashboardAggregationException")
  void customerDashboard_downstreamFailure_wrapsException() {
    when(customerClient.getById(22L, "token")).thenThrow(new RestClientException("timeout"));

    assertThrows(
        DashboardAggregationException.class,
        () -> service.customerDashboard(22L, "token", 5, 30));
  }

  @Test
  @DisplayName("non-critical downstream failures return partial dashboard data")
  void customerDashboard_optionalDownstreamFailure_returnsPartialData() {
    CustomerResponse customer = new CustomerResponse();
    customer.setId(11L);
    customer.setCustomerCode("CUST-11");
    customer.setFirstName("Jane");
    customer.setLastName("Doe");
    customer.setStatus("ACTIVE");
    customer.setKycStatus("VERIFIED");

    when(customerClient.getById(11L, "token")).thenReturn(customer);
    when(customerClient.findAll("token")).thenThrow(new RestClientException("customers timeout"));
    when(accountClient.customerAccounts(11L, "token"))
        .thenThrow(new RestClientException("accounts timeout"));

    DashboardResponse response = service.customerDashboard(11L, "token", 5, 30);

    assertNotNull(response);
    assertEquals("CUST-11", response.getCustomer().getCustomerCode());
    assertEquals(0, response.getSummaryCards().getTotalAccounts());
    assertEquals(1, response.getCustomerTotals().getAllCustomers());
    assertEquals(0, response.getQuickMetrics().getTotalTransactions());
  }
}
