package com.example.finance.auth.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

  private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

  private Bucket resolveBucket(String ip) {
    return cache.computeIfAbsent(ip, this::newBucket);
  }

  private Bucket newBucket(String ip) {
    // Limit: 10 requests per minute per IP
    Bandwidth limit =
        Bandwidth.builder().capacity(10).refillGreedy(10, Duration.ofMinutes(1)).build();
    return Bucket.builder().addLimit(limit).build();
  }

  @Override
  public boolean preHandle(
      @SuppressWarnings("null") HttpServletRequest request,
      @SuppressWarnings("null") HttpServletResponse response,
      @SuppressWarnings("null") Object handler)
      throws Exception {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty()) {
      ip = request.getRemoteAddr();
    }
    if (ip == null) {
      ip = "unknown";
    }

    Bucket bucket = resolveBucket(ip);
    if (bucket.tryConsume(1)) {
      return true;
    }

    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    response.setContentType("application/json");
    response.getWriter().write("{\"error\": \"Too many requests. Please try again later.\"}");
    return false;
  }
}
