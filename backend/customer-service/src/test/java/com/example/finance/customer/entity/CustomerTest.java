package com.example.finance.customer.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link Customer} entity. */
class CustomerTest {

  @Test
  void testCustomerBuilder() {
    // Given & When
    Customer customer =
        Customer.builder()
            .id(1L)
            .customerCode("CUST-ABC123")
            .firstName("John")
            .lastName("Doe")
            .email("john@example.com")
            .nationalId("123456")
            .status("ACTIVE")
            .kycStatus("PENDING")
            .build();

    // Then
    assertNotNull(customer);
    assertEquals(1L, customer.getId());
    assertEquals("CUST-ABC123", customer.getCustomerCode());
    assertEquals("John", customer.getFirstName());
    assertEquals("Doe", customer.getLastName());
    assertEquals("john@example.com", customer.getEmail());
    assertEquals("123456", customer.getNationalId());
    assertEquals("ACTIVE", customer.getStatus());
    assertEquals("PENDING", customer.getKycStatus());
  }

  @Test
  void testPrePersist_setsCreatedAt() {
    // Given
    Customer customer =
        Customer.builder()
            .customerCode("CUST-TEST")
            .firstName("Jane")
            .lastName("Smith")
            .email("jane@example.com")
            .nationalId("654321")
            .status("ACTIVE")
            .kycStatus("PENDING")
            .build();

    // When
    Instant beforePersist = Instant.now();
    customer.prePersist();
    Instant afterPersist = Instant.now();

    // Then
    assertNotNull(customer.getCreatedAt());
    assertTrue(!customer.getCreatedAt().isBefore(beforePersist.minusSeconds(1)));
    assertTrue(!customer.getCreatedAt().isAfter(afterPersist.plusSeconds(1)));
  }

  @Test
  void testEqualsAndHashCode() {
    // Given
    Customer customer1 =
        Customer.builder()
            .id(1L)
            .customerCode("CUST-001")
            .firstName("John")
            .lastName("Doe")
            .email("john@example.com")
            .nationalId("111")
            .status("ACTIVE")
            .kycStatus("PENDING")
            .build();

    Customer customer2 =
        Customer.builder()
            .id(1L)
            .customerCode("CUST-001")
            .firstName("John")
            .lastName("Doe")
            .email("john@example.com")
            .nationalId("111")
            .status("ACTIVE")
            .kycStatus("PENDING")
            .build();

    // Then
    assertEquals(customer1, customer2);
    assertEquals(customer1.hashCode(), customer2.hashCode());
  }
}
