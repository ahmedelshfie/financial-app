package com.example.finance.payment.client;

import com.example.finance.payment.dto.AccountResponse;
import com.example.finance.payment.dto.BalanceUpdateRequest;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AccountClient {
  private final RestTemplate restTemplate;

  @Value("${app.services.account-url}")
  private String accountServiceUrl;

  public AccountClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * Fetches account details by ID.
   *
   * @param id the account ID
   * @param token the authentication token
   * @return the account response (or null if not found)
   */
  @SuppressWarnings("null")
  public AccountResponse getAccount(Long id, String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    return restTemplate
        .exchange(
            accountServiceUrl + "/api/accounts/" + id,
            HttpMethod.GET,
            entity,
            AccountResponse.class)
        .getBody();
  }

  /**
   * Updates the balance of an account by applying a DEBIT/CREDIT operation.
   *
   * @param id the account ID
   * @param request the balance update payload
   * @param token the authentication token
   */
  @SuppressWarnings("null")
  public void updateBalance(Long id, BalanceUpdateRequest request, String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<BalanceUpdateRequest> entity = new HttpEntity<>(request, headers);

    restTemplate.exchange(
        accountServiceUrl + "/api/accounts/" + id + "/balance",
        HttpMethod.PATCH,
        entity,
        Void.class);
  }

  @SuppressWarnings("null")
  public void debit(Long accountId, BigDecimal amount, String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Map<String, Object>> entity =
        new HttpEntity<>(Map.of("amount", amount, "operationType", "DEBIT"), headers);
    restTemplate.exchange(
        accountServiceUrl + "/api/accounts/" + accountId + "/balance",
        HttpMethod.PATCH,
        entity,
        Void.class);
  }
}
