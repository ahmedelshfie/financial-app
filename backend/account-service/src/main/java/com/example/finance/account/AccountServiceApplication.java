package com.example.finance.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Account Service microservice.
 *
 * <p>This service is responsible for managing bank accounts: creating accounts, querying account
 * details by ID or customer, and applying balance updates (DEBIT / CREDIT operations). Requests are
 * authenticated via JWT bearer tokens issued by a separate authentication service.
 *
 * <p>Start the service by running this class directly or via {@code mvn spring-boot:run}.
 */
@SpringBootApplication
public class AccountServiceApplication {

  /**
   * Application entry point.
   *
   * @param args command-line arguments passed to {@link SpringApplication#run}
   */
  public static void main(String[] args) {
    SpringApplication.run(AccountServiceApplication.class, args);
  }
}
