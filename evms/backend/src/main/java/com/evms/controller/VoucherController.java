package com.evms.controller;

import com.evms.dto.request.RejectRequest;
import com.evms.dto.request.VoucherCreateRequest;
import com.evms.dto.request.VoucherUpdateRequest;
import com.evms.dto.response.PaginatedResponse;
import com.evms.dto.response.VoucherResponse;
import com.evms.security.UserPrincipal;
import com.evms.service.VoucherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * REST controller for voucher management endpoints.
 * <p>
 * Provides full CRUD operations plus workflow actions (submit, approve, reject).
 * All endpoints require JWT authentication. Role-based access is enforced
 * using {@code @PreAuthorize} annotations.
 * <p>
 * Replaces the Node.js {@code routes/vouchers.js} router.
 * <p>
 * Endpoint summary:
 * <ul>
 *   <li>{@code POST   /api/vouchers}                    — Create a new DRAFT voucher (Employee)</li>
 *   <li>{@code GET    /api/vouchers}                    — List vouchers (role-scoped, filtered, paginated)</li>
 *   <li>{@code GET    /api/vouchers/dashboard/summary}  — Dashboard KPIs (role-aware)</li>
 *   <li>{@code GET    /api/vouchers/{id}}               — Get single voucher</li>
 *   <li>{@code PUT    /api/vouchers/{id}}               — Update DRAFT voucher (Employee, owner)</li>
 *   <li>{@code DELETE /api/vouchers/{id}}               — Delete DRAFT voucher (Employee, owner)</li>
 *   <li>{@code POST   /api/vouchers/{id}/submit}        — Submit for approval (Employee, owner)</li>
 *   <li>{@code POST   /api/vouchers/{id}/approve}       — Approve voucher (Director)</li>
 *   <li>{@code POST   /api/vouchers/{id}/reject}        — Reject voucher (Director)</li>
 * </ul>
 *
 * @author EVMS Team
 */
@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {

    /** Voucher business logic service. */
    private final VoucherService voucherService;

    /**
     * Constructs the VoucherController with the VoucherService dependency.
     *
     * @param voucherService the voucher business logic service
     */
    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    // -----------------------------------------------------------------------
    // CREATE — POST /api/vouchers (Employee only)
    // -----------------------------------------------------------------------

    /**
     * Creates a new expense voucher in DRAFT status.
     * <p>
     * Only employees can create vouchers. The voucher number is auto-generated.
     *
     * @param request   the voucher creation request body
     * @param principal the authenticated employee's identity
     * @return HTTP 201 with the created voucher
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<VoucherResponse> createVoucher(
            @RequestPart("data") VoucherCreateRequest request,
            @RequestPart("proofDocument") MultipartFile proofDocument,
            @AuthenticationPrincipal UserPrincipal principal) {
        VoucherResponse response = voucherService.createVoucher(request, proofDocument, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // -----------------------------------------------------------------------
    // READ — GET /api/vouchers (role-scoped, filtered, sorted, paginated)
    // -----------------------------------------------------------------------

    /**
     * Lists vouchers with role-based scoping, dynamic filtering, sorting, and pagination.
     * <p>
     * Employees see only their own vouchers. Directors and Accounts see all.
     * Supports query params: voucherNumber, employeeName, department, category,
     * status, dateFrom, dateTo, amountMin, amountMax, sortBy, sortDir, page, limit.
     *
     * @param params    all query parameters as a map
     * @param principal the authenticated user's identity
     * @return paginated list of vouchers
     */
    @GetMapping
    public ResponseEntity<PaginatedResponse<VoucherResponse>> listVouchers(
            @RequestParam Map<String, String> params,
            @AuthenticationPrincipal UserPrincipal principal) {
        PaginatedResponse<VoucherResponse> response = voucherService.listVouchers(principal, params);
        return ResponseEntity.ok(response);
    }

    // -----------------------------------------------------------------------
    // DASHBOARD — GET /api/vouchers/dashboard/summary (role-aware)
    // -----------------------------------------------------------------------

    /**
     * Returns role-specific dashboard summary KPIs.
     * <p>
     * Different JSON shapes are returned based on the user's role.
     *
     * @param principal the authenticated user's identity
     * @return role-specific dashboard summary as a map
     */
    @GetMapping("/dashboard/summary")
    public ResponseEntity<Map<String, Object>> getDashboardSummary(
            @AuthenticationPrincipal UserPrincipal principal) {
        Map<String, Object> summary = voucherService.getDashboardSummary(principal);
        return ResponseEntity.ok(summary);
    }

    // -----------------------------------------------------------------------
    // READ — GET /api/vouchers/{id} (role-scoped)
    // -----------------------------------------------------------------------

    /**
     * Retrieves a single voucher by ID.
     * <p>
     * Employees can only view their own vouchers. Directors and Accounts
     * can view any voucher.
     *
     * @param id        the voucher ID
     * @param principal the authenticated user's identity
     * @return the requested voucher
     */
    @GetMapping("/{id}")
    public ResponseEntity<VoucherResponse> getVoucher(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        VoucherResponse response = voucherService.getVoucher(id, principal);
        return ResponseEntity.ok(response);
    }

    // -----------------------------------------------------------------------
    // UPDATE — PUT /api/vouchers/{id} (Employee, owner, DRAFT only)
    // -----------------------------------------------------------------------

    /**
     * Updates a DRAFT voucher. Only the owning employee can edit.
     *
     * @param id        the voucher ID
     * @param request   the update request body (all fields optional)
     * @param principal the authenticated employee's identity
     * @return the updated voucher
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<VoucherResponse> updateVoucher(
            @PathVariable Long id,
            @RequestPart("data") VoucherUpdateRequest request,
            @RequestPart(value = "proofDocument", required = false) MultipartFile proofDocument,
            @AuthenticationPrincipal UserPrincipal principal) {
        VoucherResponse response = voucherService.updateVoucher(id, request, proofDocument, principal);
        return ResponseEntity.ok(response);
    }

    // -----------------------------------------------------------------------
    // DELETE — DELETE /api/vouchers/{id} (Employee, owner, DRAFT only)
    // -----------------------------------------------------------------------

    /**
     * Deletes a DRAFT voucher. Only the owning employee can delete.
     *
     * @param id        the voucher ID
     * @param principal the authenticated employee's identity
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Void> deleteVoucher(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        voucherService.deleteVoucher(id, principal);
        return ResponseEntity.noContent().build();
    }

    // -----------------------------------------------------------------------
    // SUBMIT — POST /api/vouchers/{id}/submit (Employee, multipart)
    // -----------------------------------------------------------------------

    /**
     * Submits a DRAFT voucher for director approval.
     * <p>
     * Accepts a multipart form with an optional "signature" file field.
     * Transitions the voucher from DRAFT → SUBMITTED.
     *
     * @param id            the voucher ID
     * @param signature     the employee's signature image (optional if already attached)
     * @param principal     the authenticated employee's identity
     * @return the updated voucher
     */
    @PostMapping("/{id}/submit")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<VoucherResponse> submitVoucher(
            @PathVariable Long id,
            @RequestPart(value = "signature", required = false) MultipartFile signature,
            @AuthenticationPrincipal UserPrincipal principal) {
        VoucherResponse response = voucherService.submitVoucher(id, signature, principal);
        return ResponseEntity.ok(response);
    }

    // -----------------------------------------------------------------------
    // APPROVE — POST /api/vouchers/{id}/approve (Director, multipart)
    // -----------------------------------------------------------------------

    /**
     * Approves a SUBMITTED voucher. Requires a director signature image.
     * <p>
     * Transitions the voucher from SUBMITTED → APPROVED.
     *
     * @param id            the voucher ID
     * @param signature     the director's signature image (mandatory)
     * @param principal     the authenticated director's identity
     * @return the updated voucher
     */
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<VoucherResponse> approveVoucher(
            @PathVariable Long id,
            @RequestPart(value = "signature", required = false) MultipartFile signature,
            @AuthenticationPrincipal UserPrincipal principal) {
        VoucherResponse response = voucherService.approveVoucher(id, signature, principal);
        return ResponseEntity.ok(response);
    }

    // -----------------------------------------------------------------------
    // REJECT — POST /api/vouchers/{id}/reject (Director, JSON body)
    // -----------------------------------------------------------------------

    /**
     * Rejects a SUBMITTED voucher. Requires a rejection reason.
     * <p>
     * Transitions the voucher from SUBMITTED → REJECTED.
     *
     * @param id        the voucher ID
     * @param request   the rejection request containing the reason
     * @param principal the authenticated director's identity
     * @return the updated voucher
     */
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<VoucherResponse> rejectVoucher(
            @PathVariable Long id,
            @RequestBody RejectRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        VoucherResponse response = voucherService.rejectVoucher(id, request, principal);
        return ResponseEntity.ok(response);
    }
}
