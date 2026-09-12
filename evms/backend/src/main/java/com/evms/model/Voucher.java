package com.evms.model;

import com.evms.model.enums.VoucherStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * JPA Entity representing an expense voucher in the EVMS system.
 * <p>
 * Maps to the {@code vouchers} table in MySQL. Each voucher goes through a
 * lifecycle: DRAFT → SUBMITTED → APPROVED / REJECTED.
 * <p>
 * Relationships (JPA joins):
 * <ul>
 *   <li>{@code employee} — ManyToOne join to {@link User} via {@code employee_id} FK
 *       (the employee who created this voucher)</li>
 *   <li>{@code approver} — ManyToOne join to {@link User} via {@code approved_by} FK
 *       (the director who approved/rejected this voucher)</li>
 * </ul>
 *
 * @author EVMS Team
 * @see VoucherStatus
 * @see User
 */
@Entity
@Table(name = "vouchers", indexes = {
        @Index(name = "idx_vouchers_employee", columnList = "employee_id"),
        @Index(name = "idx_vouchers_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
public class Voucher {

    /** Unique identifier for the voucher (auto-generated primary key). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique voucher number in the format "EV-{year}-{sequence}". */
    @Column(name = "voucher_number", nullable = false, unique = true)
    private String voucherNumber;

    /**
     * The employee who created this voucher.
     * JOIN: vouchers.employee_id → users.id
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    /** Denormalized snapshot of the employee's name at creation time. */
    @Column(name = "employee_name", nullable = false)
    private String employeeName;

    /** Optional employee code / ID. */
    @Column(name = "employee_code")
    private String employeeCode;

    /** Department that the expense belongs to. */
    @Column(nullable = false)
    private String department;

    /** Date the voucher was raised. */
    @Column(name = "voucher_date", nullable = false)
    private LocalDate voucherDate;

    /** Date the actual expense was incurred. Cannot be in the future. */
    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    /** Short title/name of the expense. */
    @Column(name = "expense_title", nullable = false)
    private String expenseTitle;

    /** Category of the expense (e.g., "Travel", "Meals"). */
    @Column(name = "expense_category", nullable = false)
    private String expenseCategory;

    /** Optional detailed description of the expense. */
    @Column(name = "expense_description", columnDefinition = "TEXT")
    private String expenseDescription;

    /** Monetary amount claimed. Must be greater than zero. */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    /** Current lifecycle status of the voucher. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoucherStatus status;

    /** Filename of the employee's signature image. */
    @Column(name = "employee_signature")
    private String employeeSignature;

    /** Filename of the director's signature image. */
    @Column(name = "director_signature")
    private String directorSignature;

    /** Filename of the uploaded proof document (bill/voucher image or PDF). */
    @Column(name = "proof_document")
    private String proofDocument;

    /** Timestamp when the voucher was approved. */
    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    /** Reason provided by the director when rejecting. */
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    /**
     * The director who approved or rejected this voucher.
     * JOIN: vouchers.approved_by → users.id
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approver;

    /** Timestamp when the voucher was first created. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp when the voucher was last updated. */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** Lifecycle callback — sets timestamps before first persist. */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /** Lifecycle callback — refreshes updatedAt before every update. */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
