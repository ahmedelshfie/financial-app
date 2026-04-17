package com.example.finance.dashboard.client;

import com.example.finance.dashboard.dto.CustomerResponse;
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
public class CustomerClient {

  private static final Logger log = LoggerFactory.getLogger(CustomerClient.class);

  private final RestTemplate restTemplate;

  @Value("${app.services.customer-url}")
  private String customerServiceUrl;

  public CustomerClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @SuppressWarnings("null")
  public CustomerResponse getById(Long customerId, String token) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(token);
      HttpEntity<Void> entity = new HttpEntity<>(headers);

      return restTemplate
          .exchange(
              customerServiceUrl + "/api/customers/" + customerId,
              HttpMethod.GET,
              entity,
              CustomerResponse.class)
          .getBody();
    } catch (RestClientException ex) {
      log.error("Failed to fetch customer {} from customer service: {}", customerId, ex.getMessage());
      throw ex;
    }
  }

  @SuppressWarnings("null")
  public List<CustomerResponse> findAll(String token) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(token);
      HttpEntity<Void> entity = new HttpEntity<>(headers);

      ResponseEntity<List<CustomerResponse>> response =
          restTemplate.exchange(
              customerServiceUrl + "/api/customers",
              HttpMethod.GET,
              entity,
              new ParameterizedTypeReference<>() {});

      return response.getBody() == null ? List.of() : response.getBody();
    } catch (RestClientException ex) {
      log.error("Failed to fetch all customers from customer service: {}", ex.getMessage());
      throw ex;
    }
  }
}
