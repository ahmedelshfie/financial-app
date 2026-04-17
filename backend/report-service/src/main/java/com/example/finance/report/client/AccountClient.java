package com.example.finance.report.client;

import com.example.finance.report.dto.AccountResponse;
import com.example.finance.report.dto.BalanceUpdateRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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

  /** Updates the balance of an account by applying a DEBIT/CREDIT operation. */
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
  public List<AccountResponse> customerAccounts(Long customerId, String token) {
    HttpHeaders h = new HttpHeaders();
    h.setBearerAuth(token);
    HttpEntity<Void> e = new HttpEntity<>(h);
    ResponseEntity<List<AccountResponse>> r =
        restTemplate.exchange(
            accountServiceUrl + "/api/accounts/customer/" + customerId,
            HttpMethod.GET,
            e,
            new ParameterizedTypeReference<>() {});
    return r.getBody() == null ? List.of() : r.getBody();
  }
}
