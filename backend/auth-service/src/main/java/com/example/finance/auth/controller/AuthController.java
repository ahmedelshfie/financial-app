package com.example.finance.auth.controller;

import com.example.finance.auth.dto.AuthResponse;
import com.example.finance.auth.dto.LoginRequest;
import com.example.finance.auth.dto.LogoutRequest;
import com.example.finance.auth.dto.RefreshTokenRequest;
import com.example.finance.auth.dto.RegisterRequest;
import com.example.finance.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST controller for handling authentication operations. */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "APIs for user authentication and authorization")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  /**
   * Registers a new user.
   *
   * @param request the registration request containing user details
   * @return the authentication response containing JWT tokens
   */
  @PostMapping("/register")
  @Operation(
      summary = "Register a new user",
      description = "Creates a new user account and returns authentication tokens")
  @ApiResponse(responseCode = "201", description = "User registered successfully")
  @ApiResponse(responseCode = "400", description = "Invalid input or user already exists")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
  }

  /**
   * Authenticates an existing user.
   *
   * @param request the login request containing credentials
   * @return the authentication response containing JWT access and refresh tokens
   */
  @PostMapping("/login")
  @Operation(summary = "Login user", description = "Authenticates user and returns JWT tokens")
  @ApiResponse(responseCode = "200", description = "Login successful")
  @ApiResponse(responseCode = "401", description = "Invalid credentials")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    return ResponseEntity.ok(authService.login(request));
  }

  /**
   * Refreshes user access tokens using a valid refresh token.
   *
   * @param request the refresh token request
   * @return the authentication response containing a new set of tokens
   */
  @PostMapping("/refresh")
  @Operation(
      summary = "Refresh access token",
      description = "Generates new access and refresh tokens using a valid refresh token")
  @ApiResponse(responseCode = "200", description = "Token refreshed successfully")
  @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
  public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
    return ResponseEntity.ok(authService.refresh(request));
  }

  /**
   * Logs out the user by invalidating the refresh token.
   *
   * @param request containing the refresh token to invalidate
   * @return 200 HTTP code indicating successful logout
   */
  @PostMapping("/logout")
  @Operation(
      summary = "Logout user",
      description = "Invalidates the refresh token and logs out the user")
  @ApiResponse(responseCode = "200", description = "Logout successful")
  public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
    authService.logout(request);
    return ResponseEntity.ok().build();
  }
}
