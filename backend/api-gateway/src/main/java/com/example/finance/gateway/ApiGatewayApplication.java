package com.example.finance.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the API Gateway. Spring Cloud Gateway routing rules are established in
 * the application.yml.
 */
@SpringBootApplication
public class ApiGatewayApplication {

  /**
   * Application entry point.
   *
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    SpringApplication.run(ApiGatewayApplication.class, args);
  }
}
