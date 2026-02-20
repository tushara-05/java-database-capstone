package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "admin") 
public class Admin {

    // Primary key, auto-incremented
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Admin username, cannot be null
    @NotNull(message = "username cannot be null")
    @Column(nullable = false, unique = true)
    private String username;

    // Admin password, write-only in JSON responses
    @NotNull(message = "password cannot be null")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    // -------------------- Constructors --------------------

    public Admin() {
        // No-args constructor required by JPA
    }

    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // -------------------- Getters & Setters --------------------

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
