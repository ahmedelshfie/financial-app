package com.example.finance.customer.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Servlet filter that validates JWT bearer tokens on every inbound request.
 *
 * <p>When a valid {@code Authorization: Bearer <token>} header is present, the filter extracts the
 * subject (username) and roles from the verified JWT claims and sets a {@link
 * UsernamePasswordAuthenticationToken} on the {@link SecurityContextHolder}. Requests without a
 * valid token continue unauthenticated — downstream security rules determine whether access is
 * ultimately permitted.
 *
 * <p>JWT parse failures are logged at {@code WARN} level rather than silently ignored so that
 * misconfigured clients can be detected in logs.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
  private static final String BEARER_PREFIX = "Bearer ";
  private static final String ROLES_CLAIM = "roles";

  private final JwtService jwtService;

  public JwtAuthenticationFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  /**
   * Intercepts each request to validate the JWT bearer token, if present.
   *
   * @param request the incoming HTTP request
   * @param response the outgoing HTTP response
   * @param filterChain the remaining filter chain
   * @throws ServletException if a servlet error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doFilterInternal(
      @SuppressWarnings("null") HttpServletRequest request,
      @SuppressWarnings("null") HttpServletResponse response,
      @SuppressWarnings("null") FilterChain filterChain)
      throws ServletException, IOException {

    String header = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (header != null && header.startsWith(BEARER_PREFIX)) {
      String token = header.substring(BEARER_PREFIX.length());
      try {
        Claims claims = jwtService.parse(token);
        Collection<SimpleGrantedAuthority> authorities = extractAuthorities(claims);

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug(
            "Authenticated request for user '{}' on '{}'",
            claims.getSubject(),
            request.getRequestURI());

      } catch (JwtException ex) {
        log.warn("JWT validation failed for '{}': {}", request.getRequestURI(), ex.getMessage());
        // Do not set authentication — the security filter chain will reject the request
      }
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Extracts the role-based authorities from the JWT claims.
   *
   * @param claims the verified JWT payload
   * @return a collection of {@link SimpleGrantedAuthority} objects
   */
  @SuppressWarnings("unchecked")
  private Collection<SimpleGrantedAuthority> extractAuthorities(Claims claims) {
    Object rolesObj = claims.get(ROLES_CLAIM);
    if (rolesObj instanceof Collection<?> roles) {
      return roles.stream()
          .map(Object::toString)
          .map(SimpleGrantedAuthority::new)
          .collect(Collectors.toList());
    }
    return List.of();
  }
}
