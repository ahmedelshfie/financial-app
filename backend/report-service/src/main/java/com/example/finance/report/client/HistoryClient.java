package com.example.finance.report.client;

import com.example.finance.report.dto.TransactionResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HistoryClient {
  private final RestTemplate restTemplate;

  @Value("${app.services.transaction-url}")
  private String transactionServiceUrl;

  @Value("${app.services.transfer-url}")
  private String transferServiceUrl;

  @Value("${app.services.payment-url}")
  private String paymentServiceUrl;

  public HistoryClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @SuppressWarnings("null")
  public int countForAccount(String base, String path, String token) {
    HttpHeaders h = new HttpHeaders();
    h.setBearerAuth(token);
    HttpEntity<Void> e = new HttpEntity<>(h);
    @SuppressWarnings("null")
    ResponseEntity<List<TransactionResponse>> r =
        restTemplate.exchange(
            base + path, HttpMethod.GET, e, new ParameterizedTypeReference<>() {});
    return r.getBody() == null ? 0 : r.getBody().size();
  }

  public int transactions(Long accountId, String token) {
    return countForAccount(transactionServiceUrl, "/api/transactions/account/" + accountId, token);
  }

  public int transfers(Long accountId, String token) {
    return countForAccount(transferServiceUrl, "/api/transfers/account/" + accountId, token);
  }

  public int payments(Long accountId, String token) {
    return countForAccount(paymentServiceUrl, "/api/payments/account/" + accountId, token);
  }
}
