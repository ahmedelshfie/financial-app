package com.example.finance.gateway.security;

import static org.junit.jupiter.api.Assertions.*;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtServiceTest {

  private JwtService jwtService;
  private final String SECRET = "my-test-secret-key-that-is-at-least-32-bytes-long";

  @BeforeEach
  void setUp() {
    jwtService = new JwtService();
    ReflectionTestUtils.setField(jwtService, "secret", SECRET);
    jwtService.init();
  }

  @Test
  void testValidToken() {
    SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());
    String token =
        Jwts.builder()
            .subject("testUser")
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 10000))
            .signWith(key)
            .compact();

    assertTrue(jwtService.isValid(token));
  }

  @Test
  void testInvalidTokenSignature() {
    SecretKey wrongKey =
        Keys.hmacShaKeyFor("wrong-secret-key-that-is-also-at-least-32-bytes-long".getBytes());
    String token =
        Jwts.builder()
            .subject("testUser")
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 10000))
            .signWith(wrongKey)
            .compact();

    assertFalse(jwtService.isValid(token));
  }

  @Test
  void testExpiredToken() {
    SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());
    String token =
        Jwts.builder()
            .subject("testUser")
            .issuedAt(new Date(System.currentTimeMillis() - 20000))
            .expiration(new Date(System.currentTimeMillis() - 10000))
            .signWith(key)
            .compact();

    assertFalse(jwtService.isValid(token));
  }

  @Test
  void testMalformedToken() {
    assertFalse(jwtService.isValid("this.is.not.a.valid.token"));
  }

  @Test
  void testEmptyToken() {
    assertFalse(jwtService.isValid(""));
  }

  @Test
  void testInitWithShortSecretPaddsCorrectly() {
    JwtService shortSecretService = new JwtService();
    ReflectionTestUtils.setField(shortSecretService, "secret", "short");
    shortSecretService.init(); // Should not throw exception, will pad internally

    SecretKey shortPaddedKey = Keys.hmacShaKeyFor(String.format("%-32s", "short").getBytes());

    String token = Jwts.builder().subject("testUser").signWith(shortPaddedKey).compact();

    assertTrue(shortSecretService.isValid(token));
  }
}
