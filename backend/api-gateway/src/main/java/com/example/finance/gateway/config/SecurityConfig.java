package com.example.finance.gateway.config;

import com.example.finance.gateway.security.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/** Spring Security configuration for the WebFlux-based API Gateway. */
@Configuration
public class SecurityConfig {

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(
      ServerHttpSecurity http, JwtService jwtService) {
    return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
        .headers(
            headers ->
                headers
                    .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"))
                    .frameOptions(frameOptions -> frameOptions.disable())
                    .xssProtection(xss -> xss.disable()) // Replaced by CSP in modern browsers
                    .hsts(
                        hsts ->
                            hsts.includeSubdomains(true).maxAge(java.time.Duration.ofDays(365))))
        .authorizeExchange(
            ex ->
                ex.pathMatchers("/api/auth/**", "/actuator/**", "/fallback")
                    .permitAll()
                    .anyExchange()
                    .access(
                        (authentication, context) -> validate(context.getExchange(), jwtService)))
        .build();
  }

  private Mono<AuthorizationDecision> validate(ServerWebExchange exchange, JwtService jwtService) {
    String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

    boolean allowed = false;

    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);
      allowed = jwtService.isValid(token);
    }

    if (!allowed) {
      // WebFlux will eventually return 401 on false AuthorizationDecision,
      // but setting status explicitly here guarantees it if the chain aborts.
      exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
    }

    return Mono.just(new AuthorizationDecision(allowed));
  }
}
