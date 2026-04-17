package com.example.finance.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

  private final String errorCode;
  private final String resourceName;

  public ResourceNotFoundException(String message) {
    super(message);
    this.errorCode = ErrorCatalog.RESOURCE_NOT_FOUND.definition().code();
    this.resourceName = null;
  }

  public ResourceNotFoundException(String message, String errorCode) {
    super(message);
    this.errorCode = errorCode;
    this.resourceName = null;
  }

  public ResourceNotFoundException(String resourceName, Object identifier) {
    super(ErrorCatalog.RESOURCE_NOT_FOUND.message(resourceName, identifier));
    this.errorCode = ErrorCatalog.RESOURCE_NOT_FOUND.definition().code();
    this.resourceName = resourceName;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public String getResourceName() {
    return resourceName;
  }
}
