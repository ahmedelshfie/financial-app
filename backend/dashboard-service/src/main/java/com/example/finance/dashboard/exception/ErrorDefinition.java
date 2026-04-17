package com.example.finance.dashboard.exception;

import org.springframework.http.HttpStatus;

public record ErrorDefinition(String code, String message, HttpStatus status, String category) {}
