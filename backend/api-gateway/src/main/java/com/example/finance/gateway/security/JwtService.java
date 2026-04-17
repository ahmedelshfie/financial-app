package com.example.finance.gateway.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Service for verifying JSON Web Tokens (JWT) for requests routed via the API Gateway. */
@Service
public class JwtService {
  private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

  @Value("${app.jwt.secret}")
  private String secret;

  private SecretKey key;

  @PostConstruct
  public void init() {
    byte[] keyBytes;
    try {
      keyBytes = Decoders.BASE64.decode(secret);
    } catch (Exception ex) {
      // Fallback if not Base64 encoded
      logger.warn(
          "JWT Secret is not Base64 encoded. Falling back to platform default charset bytes.");
      keyBytes = secret.getBytes();
    }

    // Ensure key length is at least 32 bytes (256 bits) for HMAC SHA-256
    if (keyBytes.length < 32) {
      logger.warn(
          "JWT Secret is less than 32 bytes. Padding for minimum required security strength.");
      keyBytes = String.format("%-32s", secret).getBytes();
    }

    this.key = Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * Validates an access token.
   *
   * @param token The JWT string to validate
   * @return true if valid, false otherwise (e.g., malformed, expired)
   */
  public boolean isValid(String token) {
    try {
      Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
      return true;
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
    } catch (JwtException e) {
      logger.error("JWT token error: {}", e.getMessage());
    }
    return false;
  }
}
