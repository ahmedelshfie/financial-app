package com.example.finance.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Data Transfer Object for user registration requests. */
public class RegisterRequest {

  @NotBlank(message = ValidationMessages.USERNAME_REQUIRED)
  @Size(min = 3, max = 100, message = ValidationMessages.USERNAME_SIZE)
  private String username;

  @NotBlank(message = ValidationMessages.EMAIL_REQUIRED)
  @Email(message = ValidationMessages.EMAIL_VALID)
  @Size(max = 150, message = ValidationMessages.EMAIL_SIZE)
  private String email;

  @NotBlank(message = ValidationMessages.PASSWORD_REQUIRED)
  @Size(min = 8, max = 100, message = ValidationMessages.PASSWORD_SIZE)
  @Pattern(
      regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
      message = ValidationMessages.PASSWORD_COMPLEXITY)
  private String password;

  public RegisterRequest() {}

  public RegisterRequest(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public static RegisterRequestBuilder builder() {
    return new RegisterRequestBuilder();
  }

  public RegisterRequestBuilder toBuilder() {
    return new RegisterRequestBuilder()
        .username(this.username)
        .email(this.email)
        .password(this.password);
  }

  public static class RegisterRequestBuilder {
    private String username;
    private String email;
    private String password;

    RegisterRequestBuilder() {}

    public RegisterRequestBuilder username(String username) {
      this.username = username;
      return this;
    }

    public RegisterRequestBuilder email(String email) {
      this.email = email;
      return this;
    }

    public RegisterRequestBuilder password(String password) {
      this.password = password;
      return this;
    }

    public RegisterRequest build() {
      return new RegisterRequest(username, email, password);
    }
  }
}
