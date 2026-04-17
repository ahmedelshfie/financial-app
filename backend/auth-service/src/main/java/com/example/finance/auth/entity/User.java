package com.example.finance.auth.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/** Entity representing a user in the authentication system. */
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 100)
  private String username;

  @Column(nullable = false, unique = true, length = 150)
  private String email;

  @Column(name = "password_hash", nullable = false, length = 255)
  private String passwordHash;

  @Column(nullable = false, length = 30)
  private String status = "ACTIVE";

  private Instant lastLogin;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "user_roles",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Instant updatedAt;

  public User() {}

  public User(
      Long id,
      String username,
      String email,
      String passwordHash,
      String status,
      Instant lastLogin,
      Set<Role> roles,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.passwordHash = passwordHash;
    this.status = status != null ? status : "ACTIVE";
    this.lastLogin = lastLogin;
    this.roles = roles != null ? roles : new HashSet<>();
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Instant getLastLogin() {
    return lastLogin;
  }

  public void setLastLogin(Instant lastLogin) {
    this.lastLogin = lastLogin;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  public static UserBuilder builder() {
    return new UserBuilder();
  }

  public UserBuilder toBuilder() {
    return new UserBuilder()
        .id(this.id)
        .username(this.username)
        .email(this.email)
        .passwordHash(this.passwordHash)
        .status(this.status)
        .lastLogin(this.lastLogin)
        .roles(this.roles)
        .createdAt(this.createdAt)
        .updatedAt(this.updatedAt);
  }

  public static class UserBuilder {
    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private String status = "ACTIVE";
    private Instant lastLogin;
    private Set<Role> roles = new HashSet<>();
    private Instant createdAt;
    private Instant updatedAt;

    UserBuilder() {}

    public UserBuilder id(Long id) {
      this.id = id;
      return this;
    }

    public UserBuilder username(String username) {
      this.username = username;
      return this;
    }

    public UserBuilder email(String email) {
      this.email = email;
      return this;
    }

    public UserBuilder passwordHash(String passwordHash) {
      this.passwordHash = passwordHash;
      return this;
    }

    public UserBuilder status(String status) {
      this.status = status;
      return this;
    }

    public UserBuilder lastLogin(Instant lastLogin) {
      this.lastLogin = lastLogin;
      return this;
    }

    public UserBuilder roles(Set<Role> roles) {
      this.roles = roles;
      return this;
    }

    public UserBuilder createdAt(Instant createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public UserBuilder updatedAt(Instant updatedAt) {
      this.updatedAt = updatedAt;
      return this;
    }

    public User build() {
      return new User(
          id, username, email, passwordHash, status, lastLogin, roles, createdAt, updatedAt);
    }
  }
}
