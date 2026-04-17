package com.example.finance.auth.exception;

import org.springframework.http.HttpStatus;

public record ErrorDefinition(String code, String message, HttpStatus status, String category) {}
