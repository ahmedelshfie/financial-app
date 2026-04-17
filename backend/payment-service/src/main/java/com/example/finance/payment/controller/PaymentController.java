package com.example.finance.payment.controller;

import com.example.finance.payment.dto.BeneficiaryRequest;
import com.example.finance.payment.dto.BeneficiaryResponse;
import com.example.finance.payment.dto.PaymentRequest;
import com.example.finance.payment.dto.PaymentResponse;
import com.example.finance.payment.dto.PaymentStatusUpdateRequest;
import com.example.finance.payment.exception.ErrorCatalog;
import com.example.finance.payment.service.PaymentService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@Validated
public class PaymentController {
  private static final String BEARER_PREFIX = "Bearer ";

  private final PaymentService service;

  public PaymentController(PaymentService service) {
    this.service = service;
  }

  @PostMapping("/beneficiaries")
  public ResponseEntity<BeneficiaryResponse> addBeneficiary(
      @Valid @RequestBody BeneficiaryRequest request) {
    return ResponseEntity.ok(service.addBeneficiary(request));
  }

  @GetMapping("/beneficiaries/customer/{customerId}")
  public ResponseEntity<List<BeneficiaryResponse>> beneficiaries(@PathVariable Long customerId) {
    return ResponseEntity.ok(service.listBeneficiaries(customerId));
  }

  @PostMapping
  public ResponseEntity<PaymentResponse> initiate(
      @Valid @RequestBody PaymentRequest request,
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
    return ResponseEntity.ok(service.initiate(request, extractBearerToken(authorization)));
  }

  @PatchMapping("/{reference}/status")
  public ResponseEntity<PaymentResponse> confirm(
      @PathVariable String reference, @Valid @RequestBody PaymentStatusUpdateRequest request) {
    return ResponseEntity.ok(service.confirm(reference, request));
  }

  @GetMapping("/account/{accountId}")
  public ResponseEntity<List<PaymentResponse>> history(@PathVariable Long accountId) {
    return ResponseEntity.ok(service.history(accountId));
  }

  private String extractBearerToken(String authorization) {
    if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
      throw new IllegalArgumentException(ErrorCatalog.AUTH_HEADER_INVALID.message());
    }

    String token = authorization.substring(BEARER_PREFIX.length()).trim();
    if (token.isEmpty()) {
      throw new IllegalArgumentException(ErrorCatalog.AUTH_TOKEN_EMPTY.message());
    }

    return token;
  }
}
