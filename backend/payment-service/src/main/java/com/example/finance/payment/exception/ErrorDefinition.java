package com.example.finance.payment.exception;

import org.springframework.http.HttpStatus;

public record ErrorDefinition(String code, String message, HttpStatus status, String category) {}
