package com.example.leavemanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Employee entity - maps to the "employees" table in MySQL.
 *
 * This is the "parent" side of the Employee <-> LeaveRequest relationship:
 * ONE employee can have MANY leave requests (One-to-Many).
 */
@Entity
@Table(name = "employees")
public class Employee {

    // Primary key, auto-incremented by MySQL
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "Department is required")
    @Column(name = "department", nullable = false, length = 50)
    private String department;

    @NotBlank(message = "Designation is required")
    @Column(name = "designation", nullable = false, length = 50)
    private String designation;

    @Column(name = "date_of_joining")
    private LocalDate dateOfJoining;

    /**
     * The "One" side of the relationship.
     * mappedBy = "employee" means the foreign key column lives on the
     * LeaveRequest entity (in the leave_requests table), NOT here.
     * cascade = ALL: if an employee is deleted, their leave requests are
     * deleted too (keeps the database consistent, avoids orphan rows).
     */
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LeaveRequest> leaveRequests = new ArrayList<>();

    // ----- Constructors -----

    public Employee() {
        // Required no-arg constructor for JPA/Hibernate
    }

    public Employee(String fullName, String email, String department, String designation, LocalDate dateOfJoining) {
        this.fullName = fullName;
        this.email = email;
        this.department = department;
        this.designation = designation;
        this.dateOfJoining = dateOfJoining;
    }

    // ----- Getters and Setters -----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public LocalDate getDateOfJoining() {
        return dateOfJoining;
    }

    public void setDateOfJoining(LocalDate dateOfJoining) {
        this.dateOfJoining = dateOfJoining;
    }

    public List<LeaveRequest> getLeaveRequests() {
        return leaveRequests;
    }

    public void setLeaveRequests(List<LeaveRequest> leaveRequests) {
        this.leaveRequests = leaveRequests;
    }
}
