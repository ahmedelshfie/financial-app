package com.example.finance.account.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service responsible for parsing and verifying JWT bearer tokens.
 *
 * <p>The signing key is derived from the {@code app.jwt.secret} property. If the property value is
 * valid Base64, it is decoded first; otherwise the raw UTF-8 bytes are used, zero-padded to the
 * minimum HMAC-SHA256 key size of 32 bytes.
 *
 * <p>This class only <em>validates</em> tokens — it does not issue them. Token issuance is handled
 * by a dedicated authentication service.
 *
 * @see JwtAuthenticationFilter
 */
@Service
public class JwtService {

  /**
   * Raw secret value injected from configuration. Suppressed: Sonar flags any field containing
   * "secret" regardless of context.
   */
  @SuppressWarnings("java:S2068")
  @Value("${app.jwt.secret}")
  private String secret;

  /** HMAC-SHA key derived from {@link #secret} during {@link #init()}. */
  private SecretKey key;

  /**
   * Derives the HMAC-SHA256 signing key from the configured secret.
   *
   * <p>Called automatically by Spring after dependency injection is complete. Attempts Base64
   * decoding first; falls back to raw UTF-8 bytes padded to 32 characters if decoding fails.
   */
  @PostConstruct
  public void init() {
    byte[] keyBytes;
    try {
      keyBytes = Decoders.BASE64.decode(secret);
    } catch (Exception ex) {
      keyBytes = secret.getBytes();
    }

    key =
        Keys.hmacShaKeyFor(
            keyBytes.length >= 32 ? keyBytes : String.format("%-32s", secret).getBytes());
  }

  /**
   * Parses and verifies a compact JWT, returning its claims payload.
   *
   * <p>Throws a {@link io.jsonwebtoken.JwtException} (or a subtype) if the token is malformed, the
   * signature does not match, or the token has expired.
   *
   * @param token the compact JWT string (without the {@code Bearer } prefix)
   * @return the verified {@link Claims} payload
   * @throws io.jsonwebtoken.JwtException if the token is invalid or expired
   */
  public Claims parse(String token) {
    return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
  }
}
