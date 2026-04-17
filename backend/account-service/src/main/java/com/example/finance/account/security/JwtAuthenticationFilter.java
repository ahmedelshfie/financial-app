package com.example.finance.account.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Servlet filter that intercepts every incoming HTTP request and, when a valid JWT bearer token is
 * present, populates the Spring Security context with the authenticated principal and their granted
 * authorities.
 *
 * <p>The filter runs exactly once per request ({@link OncePerRequestFilter}). If the token is
 * absent or invalid the filter allows the request to continue unauthenticated — downstream security
 * rules decide whether to permit or reject it.
 *
 * <p>Expected header format:
 *
 * <pre>{@code Authorization: Bearer <compact-jwt>}</pre>
 *
 * @see JwtService
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  /** The prefix that identifies a JWT bearer token in the Authorization header. */
  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtService jwtService;

  /**
   * Constructs the filter with the required JWT parsing service.
   *
   * @param jwtService service used to parse and validate JWT tokens
   */
  public JwtAuthenticationFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  /**
   * Processes the incoming request by checking for a JWT bearer token.
   *
   * <p>If a valid bearer token is present, the {@link SecurityContextHolder} is populated with a
   * {@link UsernamePasswordAuthenticationToken} carrying:
   *
   * <ul>
   *   <li>principal — the JWT subject ({@code sub} claim)
   *   <li>credentials — {@code null} (not needed after token validation)
   *   <li>authorities — mapped from the {@code roles} claim, if present
   * </ul>
   *
   * <p>Any exception thrown during token parsing is silently swallowed so that the filter chain can
   * continue and downstream security can reject the request.
   *
   * @param request the incoming HTTP request
   * @param response the HTTP response
   * @param filterChain the remaining filter chain
   * @throws ServletException if a servlet error occurs further down the chain
   * @throws IOException if an I/O error occurs further down the chain
   */
  @Override
  protected void doFilterInternal(
      @SuppressWarnings("null") HttpServletRequest request,
      @SuppressWarnings("null") HttpServletResponse response,
      @SuppressWarnings("null") FilterChain filterChain)
      throws ServletException, IOException {

    String header = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (header != null && header.startsWith(BEARER_PREFIX)) {
      try {
        Claims claims = jwtService.parse(header.substring(BEARER_PREFIX.length()));
        Object roles = claims.get("roles");
        Collection<SimpleGrantedAuthority> authorities = List.of();

        if (roles instanceof Collection<?> roleCollection) {
          authorities =
              roleCollection.stream()
                  .map(Object::toString)
                  .map(SimpleGrantedAuthority::new)
                  .toList();
        }

        SecurityContextHolder.getContext()
            .setAuthentication(
                new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities));
      } catch (Exception ignored) {
        // Invalid token — leave SecurityContext empty so the request proceeds
        // unauthenticated
      }
    }

    filterChain.doFilter(request, response);
  }
}
