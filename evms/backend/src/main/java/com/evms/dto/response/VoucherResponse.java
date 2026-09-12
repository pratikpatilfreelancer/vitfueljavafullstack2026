package com.evms.dto.response;

import com.evms.model.Voucher;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response DTO for voucher data returned to the frontend.
 * Uses {@code @JsonProperty} to match the exact field names the React frontend expects.
 *
 * @author EVMS Team
 */
@Getter
@NoArgsConstructor
public class VoucherResponse {

    private Long id;

    @JsonProperty("voucher_number")
    private String voucherNumber;

    @JsonProperty("employee_id")
    private Long employeeId;

    @JsonProperty("employee_name")
    private String employeeName;

    @JsonProperty("employee_code")
    private String employeeCode;

    private String department;

    @JsonProperty("voucher_date")
    private String voucherDate;

    @JsonProperty("expense_date")
    private String expenseDate;

    @JsonProperty("expense_title")
    private String expenseTitle;

    @JsonProperty("expense_category")
    private String expenseCategory;

    @JsonProperty("expense_description")
    private String expenseDescription;

    private BigDecimal amount;
    private String status;

    @JsonProperty("employee_signature")
    private String employeeSignature;

    @JsonProperty("director_signature")
    private String directorSignature;

    @JsonProperty("proof_document")
    private String proofDocument;

    @JsonProperty("approval_date")
    private String approvalDate;

    @JsonProperty("rejection_reason")
    private String rejectionReason;

    @JsonProperty("approved_by")
    private Long approvedBy;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    private String employeeSignatureUrl;
    private String directorSignatureUrl;
    private String proofDocumentUrl;

    /**
     * Factory method to convert a {@link Voucher} JPA entity into this response DTO.
     *
     * @param v the Voucher entity to convert
     * @return a VoucherResponse DTO ready for JSON serialisation
     */
    public static VoucherResponse fromEntity(Voucher v) {
        VoucherResponse r = new VoucherResponse();
        r.id = v.getId();
        r.voucherNumber = v.getVoucherNumber();
        r.employeeId = v.getEmployee() != null ? v.getEmployee().getId() : null;
        r.employeeName = v.getEmployeeName();
        r.employeeCode = v.getEmployeeCode();
        r.department = v.getDepartment();
        r.voucherDate = v.getVoucherDate() != null ? v.getVoucherDate().toString() : null;
        r.expenseDate = v.getExpenseDate() != null ? v.getExpenseDate().toString() : null;
        r.expenseTitle = v.getExpenseTitle();
        r.expenseCategory = v.getExpenseCategory();
        r.expenseDescription = v.getExpenseDescription();
        r.amount = v.getAmount();
        r.status = v.getStatus() != null ? v.getStatus().name() : null;
        r.employeeSignature = v.getEmployeeSignature();
        r.directorSignature = v.getDirectorSignature();
        r.proofDocument = v.getProofDocument();
        r.approvalDate = v.getApprovalDate() != null ? v.getApprovalDate().toString() : null;
        r.rejectionReason = v.getRejectionReason();
        r.approvedBy = v.getApprover() != null ? v.getApprover().getId() : null;
        r.createdAt = v.getCreatedAt() != null ? v.getCreatedAt().toString() : null;
        r.updatedAt = v.getUpdatedAt() != null ? v.getUpdatedAt().toString() : null;
        r.employeeSignatureUrl = v.getEmployeeSignature() != null
                ? "/uploads/signatures/" + v.getEmployeeSignature() : null;
        r.directorSignatureUrl = v.getDirectorSignature() != null
                ? "/uploads/signatures/" + v.getDirectorSignature() : null;
        r.proofDocumentUrl = v.getProofDocument() != null
                ? "/uploads/proofs/" + v.getProofDocument() : null;
        return r;
    }
}
