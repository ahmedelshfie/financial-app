package com.example.finance.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.finance.auth.dto.AuthResponse;
import com.example.finance.auth.dto.LoginRequest;
import com.example.finance.auth.dto.RefreshTokenRequest;
import com.example.finance.auth.dto.RegisterRequest;
import com.example.finance.auth.exception.GlobalExceptionHandler;
import com.example.finance.auth.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @Mock private AuthService authService;

  @InjectMocks private AuthController authController;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    mockMvc =
        MockMvcBuilders.standaloneSetup(authController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @SuppressWarnings("null")
  @Test
  void register_Success() throws Exception {
    RegisterRequest request = new RegisterRequest();
    request.setUsername("testuser");
    request.setEmail("test@email.com");
    request.setPassword("P@ssword123!");

    AuthResponse response =
        AuthResponse.builder()
            .accessToken("jwt-access")
            .refreshToken("jwt-refresh")
            .username("testuser")
            .roles(Set.of("ROLE_CUSTOMER"))
            .build();

    when(authService.register(any(RegisterRequest.class))).thenReturn(response);

    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.accessToken").value("jwt-access"))
        .andExpect(jsonPath("$.username").value("testuser"));
  }

  @SuppressWarnings("null")
  @Test
  void login_Success() throws Exception {
    LoginRequest request = new LoginRequest();
    request.setUsername("testuser");
    request.setPassword("P@ssword123!");

    AuthResponse response =
        AuthResponse.builder()
            .accessToken("jwt-access")
            .refreshToken("jwt-refresh")
            .username("testuser")
            .roles(Set.of("ROLE_CUSTOMER"))
            .build();

    when(authService.login(any(LoginRequest.class))).thenReturn(response);

    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("jwt-access"))
        .andExpect(jsonPath("$.username").value("testuser"));
  }

  @SuppressWarnings("null")
  @Test
  void refresh_Success() throws Exception {
    RefreshTokenRequest request = new RefreshTokenRequest();
    request.setRefreshToken("refresh-token");

    AuthResponse response =
        AuthResponse.builder()
            .accessToken("new-jwt-access")
            .refreshToken("new-jwt-refresh")
            .username("testuser")
            .roles(Set.of("ROLE_CUSTOMER"))
            .build();

    when(authService.refresh(any(RefreshTokenRequest.class))).thenReturn(response);

    mockMvc
        .perform(
            post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("new-jwt-access"))
        .andExpect(jsonPath("$.refreshToken").value("new-jwt-refresh"));
  }

  @SuppressWarnings("null")
  @Test
  void register_ValidationFailure() throws Exception {
    RegisterRequest request = new RegisterRequest();
    request.setUsername("ab"); // Too short
    request.setEmail("invalid-email"); // Invalid email format
    request.setPassword("pass"); // Too short

    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
  }
}
