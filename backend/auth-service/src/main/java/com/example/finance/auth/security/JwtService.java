package com.example.finance.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Service for generating and validating JSON Web Tokens (JWT). */
@Service
public class JwtService {

  @Value("${app.jwt.secret}")
  private String secret;

  @Value("${app.jwt.access-expiration-ms}")
  private long accessExpirationMs;

  @Value("${app.jwt.refresh-expiration-ms}")
  private long refreshExpirationMs;

  private SecretKey key;

  @PostConstruct
  public void init() {
    byte[] keyBytes;
    try {
      keyBytes = Decoders.BASE64.decode(secret);
    } catch (Exception ex) {
      keyBytes = secret.getBytes();
    }

    // Ensure key is long enough for HMAC-SHA
    byte[] finalKeyBytes =
        keyBytes.length >= 32 ? keyBytes : String.format("%-32s", secret).getBytes();
    this.key = Keys.hmacShaKeyFor(finalKeyBytes);
  }

  /** Generates an access token for the specified username with custom claims. */
  public String generateAccessToken(String username, Map<String, Object> claims) {
    return buildToken(username, claims, accessExpirationMs);
  }

  /** Generates a refresh token for the specified username. */
  public String generateRefreshToken(String username) {
    return buildToken(username, new HashMap<>(), refreshExpirationMs);
  }

  private String buildToken(String username, Map<String, Object> claims, long expiration) {
    Date now = new Date();
    return Jwts.builder()
        .claims(claims)
        .subject(username)
        .issuedAt(now)
        .expiration(new Date(now.getTime() + expiration))
        .signWith(key)
        .compact();
  }

  /**
   * Validates the token and returns the username (subject).
   *
   * @throws io.jsonwebtoken.JwtException if token is invalid or expired
   */
  public String validateTokenAndGetUsername(String token) {
    Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

    return claims.getSubject();
  }
}
