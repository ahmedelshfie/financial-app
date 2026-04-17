package com.example.finance.account.config;

import com.example.finance.account.security.JwtAuthenticationFilter;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security configuration for the Account Service.
 *
 * <p>Security posture:
 *
 * <ul>
 *   <li>Stateless — no HTTP session is created or used ({@link SessionCreationPolicy#STATELESS}).
 *   <li>CSRF disabled — safe for stateless REST APIs that do not use browser-managed cookies.
 *   <li>All {@code /api/**} endpoints require a valid JWT bearer token.
 *   <li>Actuator health endpoint ({@code /actuator/health}) is open to all, enabling container
 *       orchestrators to perform liveness/readiness checks.
 *   <li>JWT claims are extracted by {@link JwtAuthenticationFilter}, which runs before the standard
 *       {@link UsernamePasswordAuthenticationFilter}.
 * </ul>
 */
@Configuration
public class SecurityConfig {
  @Value("${app.cors.allowed-origins}")
  private List<String> allowedOrigins;

  /**
   * Configures and returns the application's {@link SecurityFilterChain}.
   *
   * @param http the Spring Security HTTP builder
   * @param jwtFilter the JWT authentication pre-processor
   * @return the assembled security filter chain
   * @throws Exception if the security configuration cannot be applied
   */
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter)
      throws Exception {

    http.csrf(csrf -> csrf.disable())
        .cors(Customizer.withDefaults())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/actuator/health")
                    .permitAll()
                    .requestMatchers("/api/**")
                    .authenticated()
                    .anyRequest()
                    .permitAll())
        .httpBasic(Customizer.withDefaults())
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(allowedOrigins);
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
    config.setExposedHeaders(List.of("Location"));
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
