package com.example.leavemanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * LeaveRequest entity - maps to the "leave_requests" table in MySQL.
 *
 * This is the "child" side of the Employee <-> LeaveRequest relationship:
 * MANY leave requests belong to ONE employee (Many-to-One).
 *
 * The foreign key column "employee_id" lives in THIS table, which is why
 * this side owns the @ManyToOne + @JoinColumn mapping.
 */
@Entity
@Table(name = "leave_requests")
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Many-to-One mapping: many LeaveRequest rows can point to the same
     * Employee row. @JoinColumn creates the actual foreign key column
     * "employee_id" in the leave_requests table.
     *
     * FetchType.LAZY means the related Employee is only loaded from the
     * database when it's actually accessed (better performance).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @NotBlank(message = "Leave type is required")
    @Column(name = "leave_type", nullable = false, length = 30)
    private String leaveType; // e.g. Sick Leave, Casual Leave, Earned Leave

    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "reason", length = 255)
    private String reason;

    /**
     * Stored as a readable string (PENDING / APPROVED / REJECTED) in the
     * database instead of a numeric code, using @Enumerated(STRING).
     * This keeps the raw database table human-readable.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private LeaveStatus status = LeaveStatus.PENDING;

    @Column(name = "applied_date")
    private LocalDate appliedDate = LocalDate.now();

    // ----- Constructors -----

    public LeaveRequest() {
        // Required no-arg constructor for JPA/Hibernate
    }

    public LeaveRequest(Employee employee, String leaveType, LocalDate startDate, LocalDate endDate, String reason) {
        this.employee = employee;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = LeaveStatus.PENDING;
        this.appliedDate = LocalDate.now();
    }

    // ----- Getters and Setters -----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }

    public LocalDate getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(LocalDate appliedDate) {
        this.appliedDate = appliedDate;
    }

    /**
     * Small helper used by the Thymeleaf templates to compute how many
     * days a leave request spans (inclusive of both start and end date).
     */
    @Transient // NOT a database column - calculated on the fly
    public long getNumberOfDays() {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
}
