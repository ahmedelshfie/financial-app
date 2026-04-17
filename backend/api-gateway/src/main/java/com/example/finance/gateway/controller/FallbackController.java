package com.example.finance.gateway.controller;

import com.example.finance.gateway.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/** Fallback RestController bridging Resilience4j circuit breakers into clean JSON returns. */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

  @RequestMapping
  public Mono<ResponseEntity<ErrorResponse>> fallback() {
    ErrorResponse response =
        ErrorResponse.of(
            HttpStatus.SERVICE_UNAVAILABLE.value(),
            HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(),
            "The requested microservice is currently unavailable or timing out. Please try again"
                + " later.",
            "/fallback",
            "SERVICE_UNAVAILABLE",
            "AVAILABILITY",
            null,
            null);
    return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
  }
}
