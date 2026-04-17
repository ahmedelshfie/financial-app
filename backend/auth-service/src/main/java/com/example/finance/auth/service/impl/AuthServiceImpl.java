package com.example.finance.auth.service.impl;

import com.example.finance.auth.dto.AuthResponse;
import com.example.finance.auth.dto.LoginRequest;
import com.example.finance.auth.dto.LogoutRequest;
import com.example.finance.auth.dto.RefreshTokenRequest;
import com.example.finance.auth.dto.RegisterRequest;
import com.example.finance.auth.entity.RefreshToken;
import com.example.finance.auth.entity.Role;
import com.example.finance.auth.entity.User;
import com.example.finance.auth.exception.AuthException;
import com.example.finance.auth.exception.ErrorCatalog;
import com.example.finance.auth.exception.ResourceNotFoundException;
import com.example.finance.auth.repository.RefreshTokenRepository;
import com.example.finance.auth.repository.RoleRepository;
import com.example.finance.auth.repository.UserRepository;
import com.example.finance.auth.security.JwtService;
import com.example.finance.auth.service.AuthService;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implementation of {@link AuthService} handling business logic for authentication. */
@Service
public class AuthServiceImpl implements AuthService {

  private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthServiceImpl(
      UserRepository userRepository,
      RoleRepository roleRepository,
      RefreshTokenRepository refreshTokenRepository,
      PasswordEncoder passwordEncoder,
      JwtService jwtService) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.refreshTokenRepository = refreshTokenRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  @Value("${app.jwt.refresh-expiration-ms}")
  private long refreshExpirationMs;

  @SuppressWarnings("null")
  @Override
  @Transactional
  public AuthResponse register(RegisterRequest request) {
    if (userRepository.existsByUsername(request.getUsername())) {
      log.warn("Registration failed: username {} already exists", request.getUsername());
      throw new AuthException(ErrorCatalog.USERNAME_TAKEN);
    }
    if (userRepository.existsByEmail(request.getEmail())) {
      log.warn("Registration failed: email {} already exists", request.getEmail());
      throw new AuthException(ErrorCatalog.EMAIL_TAKEN);
    }

    Role role =
        roleRepository
            .findByName("ROLE_CUSTOMER")
            .orElseThrow(
                () -> {
                  log.error("Role ROLE_CUSTOMER not found in database");
                  return new ResourceNotFoundException(
                      ErrorCatalog.ROLE_NOT_FOUND.message("ROLE_CUSTOMER"),
                      ErrorCatalog.ROLE_NOT_FOUND.definition().code());
                });

    User user =
        User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .status("ACTIVE")
            .roles(Set.of(role))
            .build();

    userRepository.save(user);
    log.info("User registered successfully: {}", user.getUsername());
    return toResponse(user);
  }

  @Override
  @Transactional
  public AuthResponse login(LoginRequest request) {
    User user =
        userRepository
            .findByUsername(request.getUsername())
            .orElseThrow(
                () -> {
                  log.warn("Login failed: user {} not found", request.getUsername());
                  return new AuthException(ErrorCatalog.INVALID_CREDENTIALS);
                });

    if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
      log.warn("Login failed: invalid password for user {}", request.getUsername());
      throw new AuthException(ErrorCatalog.INVALID_CREDENTIALS);
    }

    user.setLastLogin(Instant.now());
    userRepository.save(user);
    log.info("User logged in successfully: {}", user.getUsername());

    return toResponse(user);
  }

  @Override
  @Transactional
  public AuthResponse refresh(RefreshTokenRequest request) {
    try {
      // Validate the token signature first
      String username = jwtService.validateTokenAndGetUsername(request.getRefreshToken());

      // Check if token exists in the database
      RefreshToken refreshTokenObj =
          refreshTokenRepository
              .findByToken(request.getRefreshToken())
              .orElseThrow(
                  () -> {
                    log.warn("Refresh token not found in database");
                    return new AuthException(ErrorCatalog.TOKEN_NOT_FOUND);
                  });

      // Check if token in DB is expired
      if (refreshTokenObj.getExpiryDate().compareTo(Instant.now()) < 0) {
        log.warn("Refresh token expired for user {}", username);
        refreshTokenRepository.delete(refreshTokenObj);
        throw new AuthException(ErrorCatalog.TOKEN_EXPIRED);
      }

      // Consume the old token
      refreshTokenRepository.delete(refreshTokenObj);

      User user =
          userRepository
              .findByUsername(username)
              .orElseThrow(
                  () -> {
                    log.error("User {} not found during token refresh", username);
                    return new AuthException(ErrorCatalog.USER_NOT_FOUND);
                  });

      log.info("Token refreshed successfully for user: {}", username);
      return toResponse(user);
    } catch (AuthException ex) {
      throw ex;
    } catch (Exception ex) {
      log.warn("Invalid refresh token: {}", ex.getMessage());
      throw new AuthException(ErrorCatalog.INVALID_TOKEN);
    }
  }

  @Override
  @Transactional
  public void logout(LogoutRequest request) {
    refreshTokenRepository.deleteByToken(request.getRefreshToken());
    log.info("User logged out successfully");
  }

  @SuppressWarnings("null")
  private AuthResponse toResponse(User user) {
    Set<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());

    String accessToken = jwtService.generateAccessToken(user.getUsername(), Map.of("roles", roles));
    String refreshTokenString = jwtService.generateRefreshToken(user.getUsername());

    RefreshToken refreshToken =
        RefreshToken.builder()
            .user(user)
            .token(refreshTokenString)
            .expiryDate(Instant.now().plusMillis(refreshExpirationMs))
            .build();

    refreshTokenRepository.save(refreshToken);

    return AuthResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshTokenString)
        .username(user.getUsername())
        .roles(roles)
        .build();
  }
}
