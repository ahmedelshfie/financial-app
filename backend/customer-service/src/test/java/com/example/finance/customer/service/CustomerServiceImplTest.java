package com.example.finance.customer.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.finance.customer.dto.CreateCustomerRequest;
import com.example.finance.customer.dto.CustomerResponse;
import com.example.finance.customer.entity.Customer;
import com.example.finance.customer.exception.CustomerNotFoundException;
import com.example.finance.customer.repository.CustomerRepository;
import com.example.finance.customer.service.impl.CustomerServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for {@link CustomerServiceImpl}. */
@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

  @Mock private CustomerRepository customerRepository;

  @InjectMocks private CustomerServiceImpl customerService;

  @BeforeEach
  void setUp() {
    customerService = new CustomerServiceImpl(customerRepository);
  }

  @SuppressWarnings("null")
  @Test
  void create_shouldSaveAndReturnCustomer() {
    // Given
    CreateCustomerRequest request = new CreateCustomerRequest();
    request.setFirstName("John");
    request.setLastName("Doe");
    request.setEmail("john@example.com");
    request.setNationalId("123456");

    Customer savedCustomer =
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

    when(customerRepository.existsByEmail(request.getEmail())).thenReturn(false);
    when(customerRepository.existsByNationalId(request.getNationalId())).thenReturn(false);
    when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

    // When
    CustomerResponse response = customerService.create(request);

    // Then
    assertNotNull(response);
    assertEquals("CUST-ABC123", response.getCustomerCode());
    assertEquals("John", response.getFirstName());
    verify(customerRepository).save(any(Customer.class));
  }

  @SuppressWarnings("null")
  @Test
  void create_withExistingEmail_shouldThrowException() {
    // Given
    CreateCustomerRequest request = new CreateCustomerRequest();
    request.setEmail("existing@example.com");

    when(customerRepository.existsByEmail(request.getEmail())).thenReturn(true);

    // When & Then
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> customerService.create(request));
    assertTrue(exception.getMessage().contains("Email already in use"));
    verify(customerRepository, never()).save(any(Customer.class));
  }

  @SuppressWarnings("null")
  @Test
  void create_withExistingNationalId_shouldThrowException() {
    // Given
    CreateCustomerRequest request = new CreateCustomerRequest();
    request.setEmail("new@example.com");
    request.setNationalId("EXISTING123");

    when(customerRepository.existsByEmail(request.getEmail())).thenReturn(false);
    when(customerRepository.existsByNationalId(request.getNationalId())).thenReturn(true);

    // When & Then
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> customerService.create(request));
    assertTrue(exception.getMessage().contains("National ID already in use"));
    verify(customerRepository, never()).save(any(Customer.class));
  }

  @Test
  void getById_shouldReturnCustomer() {
    // Given
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

    when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

    // When
    CustomerResponse response = customerService.getById(1L);

    // Then
    assertNotNull(response);
    assertEquals("CUST-ABC123", response.getCustomerCode());
  }

  @Test
  void getById_withNonExistentId_shouldThrowException() {
    // Given
    when(customerRepository.findById(999L)).thenReturn(Optional.empty());

    // When & Then
    CustomerNotFoundException exception =
        assertThrows(CustomerNotFoundException.class, () -> customerService.getById(999L));
    assertTrue(exception.getMessage().contains("Customer not found"));
  }

  @Test
  void findAll_shouldReturnAllCustomers() {
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
            .id(2L)
            .customerCode("CUST-002")
            .firstName("Jane")
            .lastName("Smith")
            .email("jane@example.com")
            .nationalId("222")
            .status("ACTIVE")
            .kycStatus("PENDING")
            .build();

    when(customerRepository.findAll()).thenReturn(List.of(customer1, customer2));

    // When
    List<CustomerResponse> responses = customerService.findAll();

    // Then
    assertNotNull(responses);
    assertEquals(2, responses.size());
    assertEquals("CUST-001", responses.get(0).getCustomerCode());
    assertEquals("CUST-002", responses.get(1).getCustomerCode());
  }
}
