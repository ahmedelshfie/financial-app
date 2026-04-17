package com.example.finance.auth.config;

import com.example.finance.auth.security.RateLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Autowired private RateLimitInterceptor rateLimitInterceptor;

  @SuppressWarnings("null")
  @Override
  public void addInterceptors(@SuppressWarnings("null") InterceptorRegistry registry) {
    registry
        .addInterceptor(rateLimitInterceptor)
        .addPathPatterns("/api/auth/login", "/api/auth/register", "/api/auth/refresh");
  }
}
