package com.example.finance.customer.config;

import com.example.finance.customer.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

/**
 * Spring Security configuration for the customer-service.
 *
 * <p>Security design decisions:
 *
 * <ul>
 *   <li>CSRF disabled — this is a stateless REST API (JWT-authenticated, no browser sessions).
 *   <li>HTTP sessions disabled — all authentication state is carried in the JWT bearer token.
 *   <li>HTTP Basic Auth disabled — only JWT bearer tokens are accepted.
 *   <li>Strict security headers applied to reduce attack surface.
 *   <li>Actuator health endpoint is public to support load-balancer health checks.
 * </ul>
 */
@Configuration
public class SecurityConfig {

  /**
   * Configures the security filter chain.
   *
   * @param http the {@link HttpSecurity} builder
   * @param jwtFilter the JWT authentication filter to add before the standard auth filter
   * @return the configured {@link SecurityFilterChain}
   * @throws Exception if configuration fails
   */
  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {

    http
        // Disable CSRF — stateless JWT API does not use cookies for session management
        .csrf(AbstractHttpConfigurer::disable)

        // Stateless sessions — no HTTP session should be created or used
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // Authorization rules
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/actuator/health")
                    .permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()
                    .requestMatchers("/api/**")
                    .authenticated()
                    .anyRequest()
                    .permitAll())

        // Security response headers
        .headers(
            headers ->
                headers
                    .contentTypeOptions(
                        ct -> {
                          /* enable X-Content-Type-Options: nosniff */
                        })
                    .frameOptions(fo -> fo.deny())
                    .referrerPolicy(
                        rp ->
                            rp.policy(
                                ReferrerPolicyHeaderWriter.ReferrerPolicy
                                    .STRICT_ORIGIN_WHEN_CROSS_ORIGIN)))

        // Register the JWT filter ahead of the standard username/password filter
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
