package com.example.finance.payment.controller;

import com.example.finance.payment.dto.TransactionResponse;
import com.example.finance.payment.dto.TransferRequest;
import com.example.finance.payment.exception.ErrorCatalog;
import com.example.finance.payment.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST controller for handling transaction operations. */
@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "APIs for managing financial transactions")
public class TransactionController {

  private static final String BEARER_PREFIX = "Bearer ";

  private final TransactionService service;

  public TransactionController(TransactionService service) {
    this.service = service;
  }

  @GetMapping
  @Operation(summary = "Get transaction base", description = "Provides guidance on transaction endpoints")
  public ResponseEntity<String> getTransactionsBase() {
    return ResponseEntity.badRequest()
        .body("{\"error\": \"Please use POST /api/transactions/transfer to transfer funds, or GET /api/transactions/account/{accountId} to view transaction history\"}");
  }

  @PostMapping("/transfer")
  @Operation(summary = "Transfer funds", description = "Executes a transfer between two accounts")
  @ApiResponse(responseCode = "200", description = "Transfer completed successfully")
  @ApiResponse(responseCode = "400", description = "Invalid transfer request")
  @ApiResponse(responseCode = "401", description = "Unauthorized")
  public ResponseEntity<TransactionResponse> transfer(
      @Valid @RequestBody TransferRequest request,
      @Parameter(description = "Bearer token for authentication", required = true)
          @RequestHeader(HttpHeaders.AUTHORIZATION)
          String authorization) {

    return ResponseEntity.ok(service.transfer(request, extractBearerToken(authorization)));
  }

  @GetMapping("/account/{accountId}")
  @Operation(
      summary = "Get transaction history",
      description = "Retrieves all transactions for a given account")
  @ApiResponse(responseCode = "200", description = "Transaction history retrieved successfully")
  @ApiResponse(responseCode = "404", description = "Account not found")
  public ResponseEntity<List<TransactionResponse>> history(@PathVariable Long accountId) {
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
