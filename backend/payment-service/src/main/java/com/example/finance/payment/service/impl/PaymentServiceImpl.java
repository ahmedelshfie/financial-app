package com.example.finance.payment.service.impl;

import com.example.finance.payment.client.AccountClient;
import com.example.finance.payment.dto.*;
import com.example.finance.payment.entity.Beneficiary;
import com.example.finance.payment.entity.Payment;
import com.example.finance.payment.exception.ErrorCatalog;
import com.example.finance.payment.repository.BeneficiaryRepository;
import com.example.finance.payment.repository.PaymentRepository;
import com.example.finance.payment.service.PaymentService;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentServiceImpl implements PaymentService {
  private final BeneficiaryRepository beneficiaryRepository;
  private final PaymentRepository paymentRepository;
  private final AccountClient accountClient;

  public PaymentServiceImpl(
      BeneficiaryRepository beneficiaryRepository,
      PaymentRepository paymentRepository,
      AccountClient accountClient) {
    this.beneficiaryRepository = beneficiaryRepository;
    this.paymentRepository = paymentRepository;
    this.accountClient = accountClient;
  }

  public BeneficiaryResponse addBeneficiary(BeneficiaryRequest request) {
    @SuppressWarnings("null")
    Beneficiary saved =
        beneficiaryRepository.save(
            Beneficiary.builder()
                .customerId(request.getCustomerId())
                .name(request.getName())
                .accountNumber(request.getAccountNumber())
                .bankCode(request.getBankCode())
                .build());
    return map(saved);
  }

  public List<BeneficiaryResponse> listBeneficiaries(Long customerId) {
    return beneficiaryRepository.findByCustomerId(customerId).stream().map(this::map).toList();
  }

  @Transactional
  public PaymentResponse initiate(PaymentRequest request, String token) {
    @SuppressWarnings("null")
    Beneficiary beneficiary =
        beneficiaryRepository
            .findById(request.getBeneficiaryId())
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        ErrorCatalog.BENEFICIARY_NOT_FOUND.message(request.getBeneficiaryId())));
    accountClient.debit(request.getSourceAccountId(), request.getAmount(), token);
    @SuppressWarnings("null")
    Payment saved =
        paymentRepository.save(
            Payment.builder()
                .paymentReference(
                    "PAY-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase(Locale.ROOT))
                .sourceAccountId(request.getSourceAccountId())
                .beneficiaryId(beneficiary.getId())
                .amount(request.getAmount())
                .currencyCode("USD")
                .status("PENDING")
                .build());
    return map(saved);
  }

  public PaymentResponse confirm(String paymentReference, PaymentStatusUpdateRequest request) {
    Payment payment =
        paymentRepository
            .findByPaymentReference(paymentReference)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        ErrorCatalog.PAYMENT_NOT_FOUND.message(paymentReference)));
    payment.setStatus(request.getStatus().toUpperCase(Locale.ROOT));
    return map(paymentRepository.save(payment));
  }

  public List<PaymentResponse> history(Long accountId) {
    return paymentRepository.findBySourceAccountId(accountId).stream().map(this::map).toList();
  }

  private BeneficiaryResponse map(Beneficiary b) {
    return BeneficiaryResponse.builder()
        .id(b.getId())
        .customerId(b.getCustomerId())
        .name(b.getName())
        .accountNumber(b.getAccountNumber())
        .bankCode(b.getBankCode())
        .build();
  }

  private PaymentResponse map(Payment p) {
    return PaymentResponse.builder()
        .id(p.getId())
        .paymentReference(p.getPaymentReference())
        .sourceAccountId(p.getSourceAccountId())
        .beneficiaryId(p.getBeneficiaryId())
        .amount(p.getAmount())
        .currencyCode(p.getCurrencyCode())
        .status(p.getStatus())
        .createdAt(p.getCreatedAt())
        .build();
  }
}
