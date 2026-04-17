package com.example.finance.customer.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.finance.customer.dto.CreateCustomerRequest;
import com.example.finance.customer.dto.CustomerResponse;
import com.example.finance.customer.security.JwtAuthenticationFilter;
import com.example.finance.customer.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** Unit tests for {@link CustomerController}. */
@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
class CustomerControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @SuppressWarnings("removal")
  @MockBean
  private CustomerService customerService;

  @SuppressWarnings("removal")
  @MockBean
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  private CreateCustomerRequest createRequest;
  private CustomerResponse customerResponse;

  @BeforeEach
  void setUp() {
    createRequest = new CreateCustomerRequest();
    createRequest.setFirstName("John");
    createRequest.setLastName("Doe");
    createRequest.setEmail("john.doe@example.com");
    createRequest.setNationalId("123456789");

    customerResponse =
        CustomerResponse.builder()
            .id(1L)
            .customerCode("CUST-ABC123")
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@example.com")
            .nationalId("123456789")
            .status("ACTIVE")
            .kycStatus("PENDING")
            .build();
  }

  @SuppressWarnings("null")
  @Test
  void create_shouldReturnCreatedCustomer() throws Exception {
    // Given
    when(customerService.create(any(CreateCustomerRequest.class))).thenReturn(customerResponse);

    // When & Then
    mockMvc
        .perform(
            post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.firstName").value("John"))
        .andExpect(jsonPath("$.lastName").value("Doe"))
        .andExpect(jsonPath("$.email").value("john.doe@example.com"));
  }

  @Test
  void getById_shouldReturnCustomer() throws Exception {
    // Given
    when(customerService.getById(1L)).thenReturn(customerResponse);

    // When & Then
    mockMvc
        .perform(get("/api/customers/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.customerCode").value("CUST-ABC123"));
  }

  @Test
  void findAll_shouldReturnCustomerList() throws Exception {
    // Given
    when(customerService.findAll()).thenReturn(List.of(customerResponse));

    // When & Then
    mockMvc
        .perform(get("/api/customers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].firstName").value("John"));
  }
}
