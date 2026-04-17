package com.example.finance.dashboard.mapper;

import com.example.finance.dashboard.dto.DashboardResponse;
import com.example.finance.dashboard.dto.PaymentResponse;
import com.example.finance.dashboard.dto.TransactionResponse;
import org.springframework.stereotype.Component;

@Component
public class DashboardMapper {

  public DashboardResponse.ActivityItem fromTransaction(TransactionResponse response) {
    return DashboardResponse.ActivityItem.builder()
        .reference(response.getTransactionReference())
        .type(response.getTransactionType())
        .amount(response.getAmount())
        .currencyCode(response.getCurrencyCode())
        .status(response.getStatus())
        .description(response.getDescription())
        .sourceAccountId(response.getSourceAccountId())
        .destinationAccountId(response.getDestinationAccountId())
        .build();
  }

  public DashboardResponse.ActivityItem fromPayment(PaymentResponse response) {
    return DashboardResponse.ActivityItem.builder()
        .reference(response.getPaymentReference())
        .type("PAYMENT")
        .amount(response.getAmount())
        .currencyCode(response.getCurrencyCode())
        .status(response.getStatus())
        .description("Payment")
        .sourceAccountId(response.getSourceAccountId())
        .destinationAccountId(null)
        .build();
  }
}
