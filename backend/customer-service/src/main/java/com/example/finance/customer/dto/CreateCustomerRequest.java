package com.example.finance.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a new customer.
 *
 * <p>All fields are mandatory and validated before the request reaches the service layer.
 */
@Schema(description = "Payload for registering a new customer in the financial system")
public class CreateCustomerRequest {

  /** Customer's first name (1–100 characters). */
  @NotBlank(message = ValidationMessages.FIRST_NAME_REQUIRED)
  @Size(max = 100, message = ValidationMessages.FIRST_NAME_MAX)
  @Schema(description = "Customer's first name", example = "John")
  private String firstName;

  /** Customer's last name (1–100 characters). */
  @NotBlank(message = ValidationMessages.LAST_NAME_REQUIRED)
  @Size(max = 100, message = ValidationMessages.LAST_NAME_MAX)
  @Schema(description = "Customer's last name", example = "Doe")
  private String lastName;

  /** Customer's email address — must be unique in the system. */
  @NotBlank(message = ValidationMessages.EMAIL_REQUIRED)
  @Email(message = ValidationMessages.EMAIL_VALID)
  @Size(max = 255, message = ValidationMessages.EMAIL_MAX)
  @Schema(description = "Customer's unique email address", example = "john.doe@example.com")
  private String email;

  /** National identification number — must be unique in the system. */
  @NotBlank(message = ValidationMessages.NATIONAL_ID_REQUIRED)
  @Size(max = 50, message = ValidationMessages.NATIONAL_ID_MAX)
  @Schema(description = "Customer's national identification number", example = "123-45-6789")
  private String nationalId;

  // ─── Constructors ─────────────────────────────────────────────────────────

  public CreateCustomerRequest() {}

  public CreateCustomerRequest(String firstName, String lastName, String email, String nationalId) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.nationalId = nationalId;
  }

  // ─── Getters & Setters ────────────────────────────────────────────────────

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getNationalId() {
    return nationalId;
  }

  public void setNationalId(String nationalId) {
    this.nationalId = nationalId;
  }
}
