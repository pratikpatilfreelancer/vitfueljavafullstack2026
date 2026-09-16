package com.evms.model;

import com.evms.model.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * JPA Entity representing a user in the EVMS system.
 * <p>
 * Maps to the {@code users} table in MySQL. Each user has a unique email
 * and is assigned one of three roles: EMPLOYEE, DIRECTOR, or ACCOUNTS.
 * <p>
 * Relationships:
 * <ul>
 *   <li>One user can create many vouchers ({@code employee_id} FK in vouchers)</li>
 *   <li>One user (director) can approve many vouchers ({@code approved_by} FK in vouchers)</li>
 * </ul>
 *
 * @author EVMS Team
 * @see Role
 * @see Voucher
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /** Unique identifier for the user (auto-generated primary key). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Full name of the user (e.g., "Asha Rao"). */
    @Column(nullable = false)
    private String name;

    /** Email address — used as the login credential. Must be unique. */
    @Column(nullable = false, unique = true)
    private String email;

    /** BCrypt-hashed password. Never stored or returned in plain text. */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /** Role assigned to this user (EMPLOYEE, DIRECTOR, or ACCOUNTS). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /** Department the user belongs to (e.g., "Engineering", "Finance"). */
    @Column
    private String department;

    /** Timestamp when the user account was created. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Sets the creation timestamp before the entity is first persisted. */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
