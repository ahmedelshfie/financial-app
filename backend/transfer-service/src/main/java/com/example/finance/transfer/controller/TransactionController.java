package com.example.finance.transfer.controller;

import com.example.finance.transfer.dto.*;
import com.example.finance.transfer.exception.ErrorCatalog;
import com.example.finance.transfer.service.TransactionService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
public class TransactionController {

  private static final String BEARER_PREFIX = "Bearer ";

  private final TransactionService service;

  public TransactionController(TransactionService service) {
    this.service = service;
  }

  @GetMapping
  public ResponseEntity<List<TransactionResponse>> listTransfers() {
    return ResponseEntity.ok(service.listAll());
  }

  @PostMapping
  public ResponseEntity<TransactionResponse> transfer(
      @Valid @RequestBody TransferRequest request,
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
    return ResponseEntity.ok(service.transfer(request, extractBearerToken(authorization)));
  }

  @PostMapping("/validate")
  public ResponseEntity<TransferValidationResponse> validate(
      @Valid @RequestBody TransferRequest request,
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
    return ResponseEntity.ok(service.validate(request, extractBearerToken(authorization)));
  }

  @GetMapping("/account/{accountId}")
  public ResponseEntity<List<TransactionResponse>> history(@PathVariable Long accountId) {
    return ResponseEntity.ok(service.history(accountId));
  }

  @GetMapping("/{reference}")
  public ResponseEntity<TransactionResponse> detail(@PathVariable String reference) {
    return ResponseEntity.ok(service.getByReference(reference));
  }

  @PatchMapping("/{reference}/status")
  public ResponseEntity<TransactionResponse> updateStatus(
      @PathVariable String reference, @Valid @RequestBody TransferStatusUpdateRequest request) {
    return ResponseEntity.ok(service.updateStatus(reference, request));
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
