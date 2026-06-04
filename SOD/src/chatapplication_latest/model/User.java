package chatapplication_latest.model;

import java.time.LocalDateTime;
import java.util.UUID;

import chatapplication_latest.enums.Role;

/**
 * Represents both USER and ADMIN accounts.
 * Role discriminates between user/admin behaviour.
 */
public class User {

    private final String id;
    private String username;         // unique across the system
    private String email;            // unique, validated format
    private String passwordHash;
    private Role role;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    public User(String username, String email, String passwordHash, Role role) {
        this.id          = UUID.randomUUID().toString();
        this.username    = username;
        this.email       = email;
        this.passwordHash = passwordHash;
        this.role        = role;
        this.active      = true;
        this.createdAt   = LocalDateTime.now();
    }

    // ── Getters ────────────────────────────────────────────────────
    public String getId()            { return id; }
    public String getUsername()      { return username; }
    public String getEmail()         { return email; }
    public String getPasswordHash()  { return passwordHash; }
    public Role   getRole()          { return role; }
    public boolean isActive()        { return active; }
    public LocalDateTime getCreatedAt()   { return createdAt; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }

    // ── Setters ────────────────────────────────────────────────────
    public void setUsername(String username)      { this.username = username; }
    public void setEmail(String email)            { this.email = email; }
    public void setPasswordHash(String hash)      { this.passwordHash = hash; }
    public void setActive(boolean active)         { this.active = active; }
    public void setLastLoginAt(LocalDateTime t)   { this.lastLoginAt = t; }

    public boolean isAdmin() { return role == Role.ADMIN; }

    @Override
    public String toString() {
        return "User{id='" + id + "', username='" + username + "', role=" + role + "}";
    }
}
