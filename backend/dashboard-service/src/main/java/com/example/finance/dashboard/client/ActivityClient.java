package com.example.finance.dashboard.client;

import com.example.finance.dashboard.dto.PaymentResponse;
import com.example.finance.dashboard.dto.TransactionResponse;
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
public class ActivityClient {

  private static final Logger log = LoggerFactory.getLogger(ActivityClient.class);

  private final RestTemplate restTemplate;

  @Value("${app.services.transaction-url}")
  private String transactionServiceUrl;

  @Value("${app.services.transfer-url}")
  private String transferServiceUrl;

  @Value("${app.services.payment-url}")
  private String paymentServiceUrl;

  public ActivityClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public List<TransactionResponse> transactions(Long accountId, String token) {
    return fetchTransactions(transactionServiceUrl + "/api/transactions/account/" + accountId, "transactions", token);
  }

  public List<TransactionResponse> transfers(Long accountId, String token) {
    return fetchTransactions(transferServiceUrl + "/api/transfers/account/" + accountId, "transfers", token);
  }

  public List<PaymentResponse> payments(Long accountId, String token) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(token);
      HttpEntity<Void> entity = new HttpEntity<>(headers);

      @SuppressWarnings("null")
      ResponseEntity<List<PaymentResponse>> response =
          restTemplate.exchange(
              paymentServiceUrl + "/api/payments/account/" + accountId,
              HttpMethod.GET,
              entity,
              new ParameterizedTypeReference<>() {});

      return response.getBody() == null ? List.of() : response.getBody();
    } catch (RestClientException ex) {
      log.error("Failed to fetch payments for account {} from payment service: {}", accountId, ex.getMessage());
      throw ex;
    }
  }

  private List<TransactionResponse> fetchTransactions(String url, String type, String token) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(token);
      HttpEntity<Void> entity = new HttpEntity<>(headers);

      @SuppressWarnings("null")
      ResponseEntity<List<TransactionResponse>> response =
          restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});

      return response.getBody() == null ? List.of() : response.getBody();
    } catch (RestClientException ex) {
      log.error("Failed to fetch {} from {} service: {}", type, type.replaceAll("s$", ""), ex.getMessage());
      throw ex;
    }
  }
}
