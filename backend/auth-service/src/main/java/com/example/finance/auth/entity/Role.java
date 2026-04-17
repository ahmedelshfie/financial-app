package com.example.finance.auth.entity;

import jakarta.persistence.*;

/** Entity representing a role assigned to users. */
@Entity
@Table(name = "roles")
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 50)
  private String name;

  @Column(length = 255)
  private String description;

  public Role() {}

  public Role(Long id, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public static RoleBuilder builder() {
    return new RoleBuilder();
  }

  public RoleBuilder toBuilder() {
    return new RoleBuilder().id(this.id).name(this.name).description(this.description);
  }

  public static class RoleBuilder {
    private Long id;
    private String name;
    private String description;

    RoleBuilder() {}

    public RoleBuilder id(Long id) {
      this.id = id;
      return this;
    }

    public RoleBuilder name(String name) {
      this.name = name;
      return this;
    }

    public RoleBuilder description(String description) {
      this.description = description;
      return this;
    }

    public Role build() {
      return new Role(id, name, description);
    }
  }
}
