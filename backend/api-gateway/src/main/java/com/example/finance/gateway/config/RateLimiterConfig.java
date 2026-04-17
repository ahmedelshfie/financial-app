package com.example.finance.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * Configuration for the Redis Request Rate Limiter. Tracks incoming traffic by extracting Client IP
 * addresses.
 */
@Configuration
public class RateLimiterConfig {

  @SuppressWarnings("null")
  @Bean
  public KeyResolver remoteAddressKeyResolver() {
    return exchange ->
        Mono.just(
            exchange.getRequest().getRemoteAddress() != null
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                : "unknown");
  }
}
