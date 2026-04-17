package com.example.finance.dashboard.client;

import com.example.finance.dashboard.dto.AccountResponse;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

@Component
public class AccountClient {

  private static final Logger log = LoggerFactory.getLogger(AccountClient.class);

  private final RestTemplate restTemplate;

  @Value("${app.services.account-url}")
  private String accountServiceUrl;

  public AccountClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @SuppressWarnings("null")
  public List<AccountResponse> customerAccounts(Long customerId, String token) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(token);
      HttpEntity<Void> entity = new HttpEntity<>(headers);

      ResponseEntity<List<AccountResponse>> response =
          restTemplate.exchange(
              accountServiceUrl + "/api/accounts/customer/" + customerId,
              HttpMethod.GET,
              entity,
              new ParameterizedTypeReference<>() {});

      return response.getBody() == null ? List.of() : response.getBody();
    } catch (RestClientException ex) {
      log.error("Failed to fetch accounts for customer {} from account service: {}", customerId, ex.getMessage());
      throw ex;
    }
  }
}
