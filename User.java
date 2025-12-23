package app.models;

import java.time.LocalDateTime;

/**
 * User Model Class
 * Represents a record from the 'users' table in bdms_schema.txt
 */

public class User {
    
    private String userId;
    private String username;
    private String password; // Note: In a real app, this should be a hash
    private String role;
    private String email;
    private LocalDateTime createdAt;
    
    // Default constructor
    public User() {
    }
    
    // Parameterized constructor
    public User(String userId, String username, String password, String role, String email) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
    }
    
    // Getters and Setters
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
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
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}