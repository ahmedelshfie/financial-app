package com.example.finance.auth.dto;

import jakarta.validation.constraints.NotBlank;

/** Data Transfer Object for logout requests. */
public class LogoutRequest {

  @NotBlank(message = ValidationMessages.REFRESH_TOKEN_REQUIRED)
  private String refreshToken;

  public LogoutRequest() {}

  public LogoutRequest(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public static LogoutRequestBuilder builder() {
    return new LogoutRequestBuilder();
  }

  public LogoutRequestBuilder toBuilder() {
    return new LogoutRequestBuilder().refreshToken(this.refreshToken);
  }

  public static class LogoutRequestBuilder {
    private String refreshToken;

    LogoutRequestBuilder() {}

    public LogoutRequestBuilder refreshToken(String refreshToken) {
      this.refreshToken = refreshToken;
      return this;
    }

    public LogoutRequest build() {
      return new LogoutRequest(refreshToken);
    }
  }
}
