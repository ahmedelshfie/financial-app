package com.example.finance.auth.security;

import static org.junit.jupiter.api.Assertions.*;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtServiceTest {

  private JwtService jwtService;
  private final String secret =
      "mySuperSecretKeyThatNeedsToBeLongEnoughToWorkProperlyWithHmacSha256";
  private final long expirationMs = 3600000;

  @SuppressWarnings("null")
  @BeforeEach
  void setUp() {
    jwtService = new JwtService();
    ReflectionTestUtils.setField(jwtService, "secret", secret);
    ReflectionTestUtils.setField(jwtService, "accessExpirationMs", expirationMs);
    ReflectionTestUtils.setField(jwtService, "refreshExpirationMs", expirationMs);
    jwtService.init();
  }

  @Test
  void testGenerateToken() {
    String username = "testuser";
    Map<String, Object> claims = Map.of("roles", "ROLE_CUSTOMER");

    String token = jwtService.generateAccessToken(username, claims);

    assertNotNull(token);

    // Get the internal key that was actually used
    @SuppressWarnings("null")
    java.security.Key internalKey =
        (java.security.Key) ReflectionTestUtils.getField(jwtService, "key");

    // Use unchecked raw parsing as we only verify it locally in test
    Claims parsedClaims =
        Jwts.parser()
            .verifyWith((javax.crypto.SecretKey) internalKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();

    assertEquals(username, parsedClaims.getSubject());
    assertEquals("ROLE_CUSTOMER", parsedClaims.get("roles"));
  }
}
