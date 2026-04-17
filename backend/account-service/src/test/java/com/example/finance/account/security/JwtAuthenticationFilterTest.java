package com.example.finance.account.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter unit tests")
class JwtAuthenticationFilterTest {

  @Mock private JwtService jwtService;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain filterChain;

  @InjectMocks private JwtAuthenticationFilter filter;

  @BeforeEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @AfterEach
  void cleanUpSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  // ─── No Authorization header ──────────────────────────────────────────────

  @Test
  @DisplayName("No Authorization header — chain continues, context remains empty")
  void noAuthHeader_chainContinues_contextEmpty() throws Exception {
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
  }

  @Test
  @DisplayName("Authorization header without Bearer prefix — chain continues, context empty")
  void authHeaderNoBearerPrefix_chainContinues_contextEmpty() throws Exception {
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic dXNlcjpwYXNz");

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
  }

  // ─── Valid token ──────────────────────────────────────────────────────────

  @Test
  @DisplayName("Valid Bearer token without roles — authentication set with empty authorities")
  void validToken_noRoles_authenticationSetWithEmptyAuthorities() throws Exception {
    Claims claims = new DefaultClaims(Map.of("sub", "user1"));

    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer valid.token.here");
    when(jwtService.parse("valid.token.here")).thenReturn(claims);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertThat(auth).isNotNull();
    assertThat(auth.getPrincipal()).isEqualTo("user1");
    assertThat(auth.getAuthorities()).isEmpty();
  }

  @Test
  @DisplayName("Valid Bearer token with roles claim — authorities populated correctly")
  void validToken_withRoles_authoritiesPopulated() throws Exception {
    Claims claims =
        new DefaultClaims(Map.of("sub", "alice", "roles", List.of("ROLE_USER", "ROLE_ADMIN")));

    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer role.token.here");
    when(jwtService.parse("role.token.here")).thenReturn(claims);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertThat(auth).isNotNull();
    assertThat(auth.getPrincipal()).isEqualTo("alice");
    assertThat(auth.getAuthorities())
        .extracting(a -> a.getAuthority())
        .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
  }

  // ─── Invalid token ────────────────────────────────────────────────────────

  @Test
  @DisplayName("Invalid token — exception swallowed, chain continues, context empty")
  void invalidToken_exceptionIgnored_contextEmpty() throws Exception {
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer bad.token");
    when(jwtService.parse("bad.token")).thenThrow(new RuntimeException("bad JWT"));

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
  }
}
