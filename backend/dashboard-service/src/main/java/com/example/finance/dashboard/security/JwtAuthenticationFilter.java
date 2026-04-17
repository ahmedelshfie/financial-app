package com.example.finance.dashboard.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  public JwtAuthenticationFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(
      @SuppressWarnings("null") HttpServletRequest request,
      @SuppressWarnings("null") HttpServletResponse response,
      @SuppressWarnings("null") FilterChain filterChain)
      throws ServletException, IOException {
    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (header != null && header.startsWith("Bearer ")) {
      try {
        Claims claims = jwtService.parse(header.substring(7));
        Object roles = claims.get("roles");
        Collection<SimpleGrantedAuthority> authorities = List.of();
        if (roles instanceof Collection<?> roleCollection) {
          authorities =
              roleCollection.stream()
                  .map(Object::toString)
                  .map(SimpleGrantedAuthority::new)
                  .collect(Collectors.toList());
        }
        SecurityContextHolder.getContext()
            .setAuthentication(
                new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities));
      } catch (Exception ignored) {
      }
    }
    filterChain.doFilter(request, response);
  }
}
