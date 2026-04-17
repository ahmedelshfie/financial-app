package com.example.finance.auth.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

import com.example.finance.auth.dto.AuthResponse;
import com.example.finance.auth.dto.LoginRequest;
import com.example.finance.auth.dto.RegisterRequest;
import com.example.finance.auth.entity.Role;
import com.example.finance.auth.entity.User;
import com.example.finance.auth.exception.AuthException;
import com.example.finance.auth.repository.RefreshTokenRepository;
import com.example.finance.auth.repository.RoleRepository;
import com.example.finance.auth.repository.UserRepository;
import com.example.finance.auth.security.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

  @Mock private UserRepository userRepository;

  @Mock private RoleRepository roleRepository;

  @Mock private RefreshTokenRepository refreshTokenRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private JwtService jwtService;

  @InjectMocks private AuthServiceImpl authService;

  private RegisterRequest registerRequest;
  private LoginRequest loginRequest;
  private User user;
  private Role role;

  @SuppressWarnings("null")
  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(authService, "refreshExpirationMs", 604800000L);

    registerRequest = new RegisterRequest();
    registerRequest.setUsername("testuser");
    registerRequest.setEmail("test@email.com");
    registerRequest.setPassword("P@ssword123!");

    loginRequest = new LoginRequest();
    loginRequest.setUsername("testuser");
    loginRequest.setPassword("P@ssword123!");

    role = Role.builder().id(1L).name("ROLE_CUSTOMER").build();
    user = User.builder().id(1L).username("testuser").passwordHash("hashed-password").build();
    user.getRoles().add(role);
  }

  @SuppressWarnings("null")
  @Test
  void testRegister_Success() {
    when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
    when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
    when(roleRepository.findByName("ROLE_CUSTOMER")).thenReturn(Optional.of(role));
    when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encoded-pwd");
    when(jwtService.generateAccessToken(any(), anyMap())).thenReturn("token");
    when(jwtService.generateRefreshToken(any())).thenReturn("refreshtoken");

    AuthResponse response = authService.register(registerRequest);

    assertNotNull(response);
    assertEquals("testuser", response.getUsername());
    assertEquals("token", response.getAccessToken());
    assertEquals("refreshtoken", response.getRefreshToken());

    verify(userRepository).save(any(User.class));
  }

  @SuppressWarnings("null")
  @Test
  void testRegister_UsernameExists() {
    when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

    AuthException ex =
        assertThrows(AuthException.class, () -> authService.register(registerRequest));
    assertEquals("USERNAME_TAKEN", ex.getErrorCode());
    assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    verify(userRepository, never()).save(any(User.class));
  }

  @SuppressWarnings("null")
  @Test
  void testLogin_Success() {
    when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash()))
        .thenReturn(true);
    when(jwtService.generateAccessToken(any(), anyMap())).thenReturn("token");
    when(jwtService.generateRefreshToken(any())).thenReturn("refreshtoken");

    AuthResponse response = authService.login(loginRequest);

    assertNotNull(response);
    assertEquals("testuser", response.getUsername());
    assertEquals("token", response.getAccessToken());
    assertEquals("refreshtoken", response.getRefreshToken());

    verify(userRepository).save(user); // Updates last login
  }

  @Test
  void testLogin_UserNotFound() {
    when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.empty());

    AuthException ex = assertThrows(AuthException.class, () -> authService.login(loginRequest));
    assertEquals("INVALID_CREDENTIALS", ex.getErrorCode());
    assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
  }

  @Test
  void testLogin_InvalidCredentials() {
    when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash()))
        .thenReturn(false);

    AuthException ex = assertThrows(AuthException.class, () -> authService.login(loginRequest));
    assertEquals("INVALID_CREDENTIALS", ex.getErrorCode());
    assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
  }
}
