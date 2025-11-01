package com.matchMenthor.matchMenthor.DTO;

import java.time.LocalDateTime;

public class UserListDto {

    private Long id;
    private String name;
    private String email;
    private String role;
    private String city;
    private boolean blocked;
    private LocalDateTime createdAt;

    public UserListDto() {
    }

    public UserListDto(Long id, String name, String email, String role, String city, boolean blocked, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.city = city;
        this.blocked = blocked;
        this.createdAt = createdAt;
    }

    // getters y setters

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
