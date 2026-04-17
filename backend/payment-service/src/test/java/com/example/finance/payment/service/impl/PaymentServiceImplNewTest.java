package com.example.finance.payment.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.finance.payment.client.AccountClient;
import com.example.finance.payment.dto.PaymentRequest;
import com.example.finance.payment.dto.PaymentStatusUpdateRequest;
import com.example.finance.payment.entity.Beneficiary;
import com.example.finance.payment.entity.Payment;
import com.example.finance.payment.repository.BeneficiaryRepository;
import com.example.finance.payment.repository.PaymentRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplNewTest {

  @Mock BeneficiaryRepository beneficiaryRepository;
  @Mock PaymentRepository paymentRepository;
  @Mock AccountClient accountClient;
  @InjectMocks PaymentServiceImpl service;

  @SuppressWarnings("null")
  @Test
  void initiate_debitsAccountAndCreatesPendingPayment() {
    when(beneficiaryRepository.findById(5L))
        .thenReturn(Optional.of(Beneficiary.builder().id(5L).build()));
    when(paymentRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

    PaymentRequest request = new PaymentRequest();
    request.setSourceAccountId(99L);
    request.setBeneficiaryId(5L);
    request.setAmount(BigDecimal.TEN);

    var response = service.initiate(request, "token");

    verify(accountClient).debit(99L, BigDecimal.TEN, "token");
    assertEquals("PENDING", response.getStatus());
  }

  @Test
  void initiate_throwsWhenBeneficiaryMissing() {
    when(beneficiaryRepository.findById(5L)).thenReturn(Optional.empty());

    PaymentRequest request = new PaymentRequest();
    request.setSourceAccountId(99L);
    request.setBeneficiaryId(5L);
    request.setAmount(BigDecimal.TEN);

    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> service.initiate(request, "token"));

    assertEquals("Beneficiary not found: 5", ex.getMessage());
  }

  @Test
  void confirm_throwsWhenPaymentReferenceMissing() {
    when(paymentRepository.findByPaymentReference("PAY-404")).thenReturn(Optional.empty());

    PaymentStatusUpdateRequest request = new PaymentStatusUpdateRequest();
    request.setStatus("COMPLETED");

    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> service.confirm("PAY-404", request));

    assertEquals("Payment not found: PAY-404", ex.getMessage());
  }

  @Test
  void history_returnsMappedPayments() {
    Payment payment =
        Payment.builder()
            .id(1L)
            .paymentReference("PAY-1")
            .sourceAccountId(42L)
            .beneficiaryId(7L)
            .amount(BigDecimal.ONE)
            .currencyCode("USD")
            .status("PENDING")
            .build();
    when(paymentRepository.findBySourceAccountId(42L)).thenReturn(List.of(payment));

    var history = service.history(42L);

    assertEquals(1, history.size());
    assertEquals("PAY-1", history.get(0).getPaymentReference());
    assertEquals("PENDING", history.get(0).getStatus());
  }
}
