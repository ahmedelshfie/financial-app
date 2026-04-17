package com.example.finance.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/**
 * Response DTO representing a customer resource returned from the API.
 *
 * <p>Carries all customer profile fields plus audit metadata.
 */
@Schema(description = "Customer profile returned in API responses")
public class CustomerResponse {

  @Schema(description = "Auto-generated surrogate primary key", example = "42")
  private Long id;

  @Schema(description = "Unique customer code assigned at creation", example = "CUST-A1B2C3D4")
  private String customerCode;

  @Schema(description = "Customer's first name", example = "John")
  private String firstName;

  @Schema(description = "Customer's last name", example = "Doe")
  private String lastName;

  @Schema(description = "Customer's email address", example = "john.doe@example.com")
  private String email;

  @Schema(description = "Customer's national identification number", example = "123-45-6789")
  private String nationalId;

  @Schema(description = "Lifecycle status: ACTIVE, INACTIVE, or SUSPENDED", example = "ACTIVE")
  private String status;

  @Schema(
      description = "KYC verification status: PENDING, VERIFIED, or REJECTED",
      example = "PENDING")
  private String kycStatus;

  @Schema(description = "UTC timestamp when the record was created")
  private Instant createdAt;

  // ─── Constructors ─────────────────────────────────────────────────────────

  public CustomerResponse() {}

  private CustomerResponse(Builder builder) {
    this.id = builder.id;
    this.customerCode = builder.customerCode;
    this.firstName = builder.firstName;
    this.lastName = builder.lastName;
    this.email = builder.email;
    this.nationalId = builder.nationalId;
    this.status = builder.status;
    this.kycStatus = builder.kycStatus;
    this.createdAt = builder.createdAt;
  }

  // ─── Getters ──────────────────────────────────────────────────────────────

  public Long getId() {
    return id;
  }

  public String getCustomerCode() {
    return customerCode;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getEmail() {
    return email;
  }

  public String getNationalId() {
    return nationalId;
  }

  public String getStatus() {
    return status;
  }

  public String getKycStatus() {
    return kycStatus;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  // ─── Builder ──────────────────────────────────────────────────────────────

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private Long id;
    private String customerCode;
    private String firstName;
    private String lastName;
    private String email;
    private String nationalId;
    private String status;
    private String kycStatus;
    private Instant createdAt;

    private Builder() {}

    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    public Builder customerCode(String customerCode) {
      this.customerCode = customerCode;
      return this;
    }

    public Builder firstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    public Builder lastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    public Builder email(String email) {
      this.email = email;
      return this;
    }

    public Builder nationalId(String nationalId) {
      this.nationalId = nationalId;
      return this;
    }

    public Builder status(String status) {
      this.status = status;
      return this;
    }

    public Builder kycStatus(String kycStatus) {
      this.kycStatus = kycStatus;
      return this;
    }

    public Builder createdAt(Instant createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public CustomerResponse build() {
      return new CustomerResponse(this);
    }
  }
}
