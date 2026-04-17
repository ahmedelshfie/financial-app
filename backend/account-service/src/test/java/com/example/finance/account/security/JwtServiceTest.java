package com.example.finance.account.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("JwtService unit tests")
class JwtServiceTest {

  /** 32-byte plain-text secret (not Base64-encoded on purpose to exercise the fallback path). */
  private static final String SECRET = "test-secret-key-that-is-long-enough!!";

  private JwtService jwtService;
  private SecretKey signingKey;

  @BeforeEach
  void setUp() {
    jwtService = new JwtService();
    // Inject secret via Spring's ReflectionTestUtils (mirrors @Value injection)
    ReflectionTestUtils.setField(jwtService, "secret", SECRET);
    jwtService.init();

    // Build the same key the service uses internally so we can sign test tokens
    signingKey =
        Keys.hmacShaKeyFor(
            SECRET.length() >= 32 ? SECRET.getBytes() : String.format("%-32s", SECRET).getBytes());
  }

  private String buildToken(String subject, long expiryMs) {
    return Jwts.builder()
        .subject(subject)
        .expiration(new Date(System.currentTimeMillis() + expiryMs))
        .signWith(signingKey)
        .compact();
  }

  private String buildTokenWithRoles(String subject, List<String> roles) {
    return Jwts.builder()
        .subject(subject)
        .claim("roles", roles)
        .expiration(new Date(System.currentTimeMillis() + 60_000))
        .signWith(signingKey)
        .compact();
  }

  // ─── parse ────────────────────────────────────────────────────────────────

  @Test
  @DisplayName("parse: valid token returns claims with correct subject")
  void parse_validToken_returnsCorrectSubject() {
    String token = buildToken("user42", 60_000);

    Claims claims = jwtService.parse(token);

    assertThat(claims.getSubject()).isEqualTo("user42");
  }

  @Test
  @DisplayName("parse: valid token with roles claim preserves roles list")
  void parse_validToken_returnsRolesClaim() {
    String token = buildTokenWithRoles("alice", List.of("ROLE_USER", "ROLE_ADMIN"));

    Claims claims = jwtService.parse(token);

    assertThat(claims.getSubject()).isEqualTo("alice");
    Object roles = claims.get("roles");
    assertThat(roles).isInstanceOf(List.class);
    @SuppressWarnings("unchecked")
    List<String> roleList = (List<String>) roles;
    assertThat(roleList).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
  }

  @Test
  @DisplayName("parse: garbage token throws JwtException")
  void parse_invalidToken_throwsJwtException() {
    assertThatThrownBy(() -> jwtService.parse("not.a.valid.token"))
        .isInstanceOf(JwtException.class);
  }

  @Test
  @DisplayName("parse: token signed with different key throws JwtException")
  void parse_wrongSignature_throwsJwtException() {
    SecretKey otherKey = Keys.hmacShaKeyFor("completely-different-key-32bytes!!".getBytes());
    String tokenWithOtherKey =
        Jwts.builder()
            .subject("hacker")
            .expiration(new Date(System.currentTimeMillis() + 60_000))
            .signWith(otherKey)
            .compact();

    assertThatThrownBy(() -> jwtService.parse(tokenWithOtherKey)).isInstanceOf(JwtException.class);
  }

  @Test
  @DisplayName("parse: expired token throws JwtException")
  void parse_expiredToken_throwsJwtException() {
    // Expires immediately (1 ms in the past by the time parse is called)
    String expiredToken = buildToken("user", -1_000);

    assertThatThrownBy(() -> jwtService.parse(expiredToken)).isInstanceOf(JwtException.class);
  }
}
