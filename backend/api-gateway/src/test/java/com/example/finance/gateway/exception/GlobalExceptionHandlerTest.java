package com.example.finance.gateway.exception;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

// Disable Spring Security Auto Config to bypass 401s during exception testing
@WebFluxTest(excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
@Import({GlobalErrorWebExceptionHandler.class, GlobalExceptionHandlerTest.TestRoutes.class})
class GlobalExceptionHandlerTest {

  @Autowired private WebTestClient webTestClient;

  @TestConfiguration
  static class TestRoutes {
    @Bean
    public RouterFunction<ServerResponse> testRouter() {
      return RouterFunctions.route(
              GET("/simulate-error"),
              request -> {
                throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_GATEWAY, "Simulated Bad Gateway");
              })
          .andRoute(
              GET("/simulate-runtime-error"),
              request -> {
                throw new RuntimeException("Simulated Runtime Exception");
              });
    }
  }

  @Test
  void testStandardResponseStatusException() {
    webTestClient
        .get()
        .uri("/simulate-error")
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.BAD_GATEWAY)
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo(502)
        .jsonPath("$.message")
        .isEqualTo("Simulated Bad Gateway")
        .jsonPath("$.errorCode")
        .isEqualTo("INTERNAL_SERVER_ERROR")
        .jsonPath("$.path")
        .isEqualTo("/simulate-error")
        .jsonPath("$.timestamp")
        .exists()
        .jsonPath("$.traceId")
        .exists();
  }

  @Test
  void testRuntimeException() {
    webTestClient
        .get()
        .uri("/simulate-runtime-error")
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo(500)
        .jsonPath("$.message")
        .exists()
        .jsonPath("$.path")
        .isEqualTo("/simulate-runtime-error")
        .jsonPath("$.timestamp")
        .exists()
        .jsonPath("$.errorCode")
        .isEqualTo("INTERNAL_SERVER_ERROR");
  }

  @Test
  void testNotFoundException() {
    webTestClient
        .get()
        .uri("/this-route-does-not-exist-at-all")
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo(404)
        .jsonPath("$.path")
        .isEqualTo("/this-route-does-not-exist-at-all")
        .jsonPath("$.timestamp")
        .exists()
        .jsonPath("$.errorCode")
        .isEqualTo("NOT_FOUND");
  }
}
