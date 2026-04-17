package com.example.finance.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Data Transfer Object for user login requests. */
public class LoginRequest {

  @NotBlank(message = ValidationMessages.USERNAME_REQUIRED)
  @Size(min = 3, max = 100, message = ValidationMessages.USERNAME_SIZE)
  private String username;

  @NotBlank(message = ValidationMessages.PASSWORD_REQUIRED)
  @Size(min = 8, max = 100, message = ValidationMessages.PASSWORD_SIZE)
  private String password;

  public LoginRequest() {}

  public LoginRequest(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public static LoginRequestBuilder builder() {
    return new LoginRequestBuilder();
  }

  public LoginRequestBuilder toBuilder() {
    return new LoginRequestBuilder().username(this.username).password(this.password);
  }

  public static class LoginRequestBuilder {
    private String username;
    private String password;

    LoginRequestBuilder() {}

    public LoginRequestBuilder username(String username) {
      this.username = username;
      return this;
    }

    public LoginRequestBuilder password(String password) {
      this.password = password;
      return this;
    }

    public LoginRequest build() {
      return new LoginRequest(username, password);
    }
  }
}
