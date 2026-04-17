package com.example.finance.auth.dto;

import jakarta.validation.constraints.NotBlank;

/** Data Transfer Object for refresh token requests. */
public class RefreshTokenRequest {

  @NotBlank(message = ValidationMessages.REFRESH_TOKEN_REQUIRED)
  private String refreshToken;

  public RefreshTokenRequest() {}

  public RefreshTokenRequest(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public static RefreshTokenRequestBuilder builder() {
    return new RefreshTokenRequestBuilder();
  }

  public RefreshTokenRequestBuilder toBuilder() {
    return new RefreshTokenRequestBuilder().refreshToken(this.refreshToken);
  }

  public static class RefreshTokenRequestBuilder {
    private String refreshToken;

    RefreshTokenRequestBuilder() {}

    public RefreshTokenRequestBuilder refreshToken(String refreshToken) {
      this.refreshToken = refreshToken;
      return this;
    }

    public RefreshTokenRequest build() {
      return new RefreshTokenRequest(refreshToken);
    }
  }
}
