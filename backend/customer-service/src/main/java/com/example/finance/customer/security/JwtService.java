package com.example.finance.customer.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service responsible for parsing and verifying JWT access tokens.
 *
 * <p>The signing key is configured via the {@code app.jwt.secret} property and must be a
 * Base64-encoded HMAC-SHA256 key of at least 256 bits (32 bytes). An {@link IllegalStateException}
 * is thrown at startup if the configured key is too short to ensure no silent security degradation.
 */
@Service
public class JwtService {

  private static final Logger log = LoggerFactory.getLogger(JwtService.class);
  private static final int MINIMUM_KEY_BYTES = 32;

  @Value("${app.jwt.secret}")
  private String secret;

  private SecretKey signingKey;

  /**
   * Initializes the signing key from the configured Base64-encoded secret. Fails fast at startup if
   * the decoded key is shorter than {@value MINIMUM_KEY_BYTES} bytes.
   *
   * @throws IllegalStateException if the configured JWT secret is too short
   */
  @PostConstruct
  public void init() {
    byte[] keyBytes;
    try {
      keyBytes = Decoders.BASE64.decode(secret);
    } catch (Exception ex) {
      log.warn("JWT secret is not valid Base64 — attempting raw byte interpretation");
      keyBytes = secret.getBytes();
    }

    if (keyBytes.length < MINIMUM_KEY_BYTES) {
      throw new IllegalStateException(
          "JWT secret must be at least "
              + MINIMUM_KEY_BYTES
              + " bytes after decoding. "
              + "Current length: "
              + keyBytes.length
              + " bytes. "
              + "Update the 'app.jwt.secret' property.");
    }

    this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    log.info("JWT signing key initialized successfully (key length: {} bytes)", keyBytes.length);
  }

  /**
   * Parses a signed JWT string and returns the verified claims payload.
   *
   * @param token the compact JWT string (without the "Bearer " prefix)
   * @return the verified {@link Claims} payload
   * @throws JwtException if the token is malformed, expired, or has an invalid signature
   */
  public Claims parse(String token) {
    return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
  }
}
