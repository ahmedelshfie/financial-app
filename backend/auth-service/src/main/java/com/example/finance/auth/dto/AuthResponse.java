package com.example.finance.auth.dto;

import java.util.Set;

/** Data Transfer Object for authentication responses. */
public class AuthResponse {

  private String accessToken;
  private String refreshToken;
  private String username;
  private Set<String> roles;

  public AuthResponse() {}

  public AuthResponse(String accessToken, String refreshToken, String username, Set<String> roles) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.username = username;
    this.roles = roles;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Set<String> getRoles() {
    return roles;
  }

  public void setRoles(Set<String> roles) {
    this.roles = roles;
  }

  public static AuthResponseBuilder builder() {
    return new AuthResponseBuilder();
  }

  public AuthResponseBuilder toBuilder() {
    return new AuthResponseBuilder()
        .accessToken(this.accessToken)
        .refreshToken(this.refreshToken)
        .username(this.username)
        .roles(this.roles);
  }

  public static class AuthResponseBuilder {
    private String accessToken;
    private String refreshToken;
    private String username;
    private Set<String> roles;

    AuthResponseBuilder() {}

    public AuthResponseBuilder accessToken(String accessToken) {
      this.accessToken = accessToken;
      return this;
    }

    public AuthResponseBuilder refreshToken(String refreshToken) {
      this.refreshToken = refreshToken;
      return this;
    }

    public AuthResponseBuilder username(String username) {
      this.username = username;
      return this;
    }

    public AuthResponseBuilder roles(Set<String> roles) {
      this.roles = roles;
      return this;
    }

    public AuthResponse build() {
      return new AuthResponse(accessToken, refreshToken, username, roles);
    }
  }
}
