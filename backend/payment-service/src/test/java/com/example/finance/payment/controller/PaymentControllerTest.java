package com.example.finance.payment.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.finance.payment.dto.BeneficiaryResponse;
import com.example.finance.payment.dto.PaymentResponse;
import com.example.finance.payment.exception.GlobalExceptionHandler;
import com.example.finance.payment.service.PaymentService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

  @Mock private PaymentService paymentService;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(new PaymentController(paymentService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void getBeneficiaries_withCustomerId_returnsBeneficiaries() throws Exception {
    BeneficiaryResponse beneficiary =
        BeneficiaryResponse.builder()
            .id(1L)
            .customerId(10L)
            .name("John Doe")
            .accountNumber("1234567890")
            .bankCode("BANK001")
            .build();
    when(paymentService.listBeneficiaries(10L)).thenReturn(List.of(beneficiary));

    mockMvc
        .perform(get("/api/payments/beneficiaries/customer/10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("John Doe"))
        .andExpect(jsonPath("$[0].accountNumber").value("1234567890"));

    verify(paymentService).listBeneficiaries(10L);
  }

  @Test
  void initiate_withUnsupportedBodyValue_returnsInvalidRequestBody() throws Exception {
    String invalidJson =
        """
        {
          "sourceAccountId": 1,
          "beneficiaryId": "BEN-100",
          "amount": 150.00
        }
        """;

    mockMvc
        .perform(
            post("/api/payments")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST_BODY"))
        .andExpect(jsonPath("$.category").value("VALIDATION"))
        .andExpect(jsonPath("$.path").value("/api/payments"));
  }

  @Test
  void getHistory_withAccountId_returnsPayments() throws Exception {
    PaymentResponse payment =
        PaymentResponse.builder()
            .id(1L)
            .paymentReference("PAY-1")
            .sourceAccountId(10L)
            .beneficiaryId(5L)
            .amount(BigDecimal.TEN)
            .currencyCode("USD")
            .status("PENDING")
            .build();
    when(paymentService.history(10L)).thenReturn(List.of(payment));

    mockMvc
        .perform(get("/api/payments/account/10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].paymentReference").value("PAY-1"))
        .andExpect(jsonPath("$[0].status").value("PENDING"));

    verify(paymentService).history(10L);
  }

  @Test
  void initiate_withoutBearerPrefix_returnsStandardBadRequest() throws Exception {
    String validJson =
        """
        {
          "sourceAccountId": 1,
          "beneficiaryId": 5,
          "amount": 150.00
        }
        """;

    mockMvc
        .perform(
            post("/api/payments")
                .header("Authorization", "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.message").value("Authorization header must use Bearer token"));
  }
}
