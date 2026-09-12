package model;

import java.time.LocalDateTime;

/**
 * Base user entity. Both Candidates and Recruiters have an underlying User
 * record used for authentication (login/registration).
 */
public class User {
    private int userId;
    private String username;
    private String password; // stored as a hash in the DB layer
    private String role;     // "CANDIDATE" or "RECRUITER"
    private String email;
    private String fullName;
    private LocalDateTime createdAt;

    public User() {}

    public User(int userId, String username, String password, String role,
                String email, String fullName, LocalDateTime createdAt) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.fullName = fullName;
        this.createdAt = createdAt;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return fullName + " (" + username + ")";
    }
}
