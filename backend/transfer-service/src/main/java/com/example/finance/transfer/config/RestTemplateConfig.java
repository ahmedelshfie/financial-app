package com.example.finance.transfer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/** Configuration for REST client components. */
@Configuration
public class RestTemplateConfig {

  /**
   * Creates a RestTemplate bean for HTTP client operations.
   *
   * @return the configured RestTemplate
   */
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
