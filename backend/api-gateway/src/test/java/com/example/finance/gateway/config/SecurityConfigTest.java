package com.example.finance.gateway.config;

import com.example.finance.gateway.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest
@Import({SecurityConfig.class, SecurityConfigTest.MockJwtServiceConfig.class})
class SecurityConfigTest {

  @Autowired private WebTestClient webTestClient;

  // Use a manual stub instead of Mockito to avoid ByteBuddy Java 26
  // incompatibility
  @Configuration
  static class MockJwtServiceConfig {
    @Bean
    @Primary
    public JwtService mockJwtService() {
      return new JwtService() {
        @Override
        public void init() {}

        @Override
        public boolean isValid(String token) {
          return "valid-token".equals(token);
        }
      };
    }
  }

  @Test
  void testPublicEndpointAllowedWithoutToken() {
    webTestClient.get().uri("/actuator/health").exchange().expectStatus().isNotFound();
  }

  @Test
  void testAuthEndpointAllowedWithoutToken() {
    webTestClient.post().uri("/api/auth/login").exchange().expectStatus().isNotFound();
  }

  @Test
  void testProtectedEndpointWithoutTokenReturnsUnauthorized() {
    webTestClient.get().uri("/api/accounts/123").exchange().expectStatus().isUnauthorized();
  }

  @Test
  void testProtectedEndpointWithInvalidTokenReturnsUnauthorized() {
    webTestClient
        .get()
        .uri("/api/accounts/123")
        .header("Authorization", "Bearer invalid-token")
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  void testProtectedEndpointWithValidTokenAllowsAccess() {
    webTestClient
        .get()
        .uri("/api/accounts/123")
        .header("Authorization", "Bearer valid-token")
        .exchange()
        .expectStatus()
        .isNotFound(); // Allowed past security, no handler found
  }
}
