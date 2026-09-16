package com.evms.service;

import com.evms.dto.request.RejectRequest;
import com.evms.dto.request.VoucherCreateRequest;
import com.evms.dto.request.VoucherUpdateRequest;
import com.evms.dto.response.PaginatedResponse;
import com.evms.dto.response.VoucherResponse;
import com.evms.exception.AccessDeniedException;
import com.evms.exception.BadRequestException;
import com.evms.exception.InvalidStateException;
import com.evms.exception.ResourceNotFoundException;
import com.evms.model.User;
import com.evms.model.Voucher;
import com.evms.model.VoucherCounter;
import com.evms.model.enums.Role;
import com.evms.model.enums.VoucherStatus;
import com.evms.repository.UserRepository;
import com.evms.repository.VoucherCounterRepository;
import com.evms.repository.VoucherRepository;
import com.evms.security.UserPrincipal;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Core business logic service for voucher management.
 * <p>
 * Handles all voucher CRUD operations, state transitions (submit, approve, reject),
 * dashboard summary computation, and role-based access control. This service is the
 * Java equivalent of the Node.js {@code routes/vouchers.js} file.
 * <p>
 * All business rules from the original implementation are preserved:
 * <ul>
 *   <li>Voucher numbers are auto-generated (EV-{year}-{seq})</li>
 *   <li>New vouchers start as DRAFT</li>
 *   <li>Employees can only see/edit/delete their own DRAFT vouchers</li>
 *   <li>Only Directors can approve/reject SUBMITTED vouchers</li>
 *   <li>Approving requires a signature image; rejecting requires a reason</li>
 * </ul>
 *
 * @author EVMS Team
 */
@Service
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherCounterRepository counterRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    /** Whitelist of database columns that can be used for sorting. */
    private static final Set<String> ALLOWED_SORT_COLUMNS = Set.of(
            "voucherNumber", "employeeName", "department",
            "expenseDate", "amount", "status", "createdAt"
    );

    /**
     * Constructs the VoucherService with required dependencies.
     *
     * @param voucherRepository JPA repository for vouchers
     * @param counterRepository JPA repository for voucher counters
     * @param userRepository    JPA repository for users
     * @param fileStorageService file storage service for signatures
     */
    public VoucherService(VoucherRepository voucherRepository,
                          VoucherCounterRepository counterRepository,
                          UserRepository userRepository,
                          FileStorageService fileStorageService) {
        this.voucherRepository = voucherRepository;
        this.counterRepository = counterRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    // -----------------------------------------------------------------------
    // CREATE — POST /api/vouchers (Employee only)
    // -----------------------------------------------------------------------

    /**
     * Creates a new expense voucher in DRAFT status.
     * <p>
     * Auto-generates a unique voucher number (EV-{year}-{seq}) and validates
     * all mandatory fields. The voucher is owned by the requesting employee.
     *
     * @param request   the voucher creation request body
     * @param principal the authenticated employee's identity
     * @return the created voucher as a response DTO
     * @throws BadRequestException if validation fails
     */
    @Transactional
    public VoucherResponse createVoucher(VoucherCreateRequest request, MultipartFile proofDocument, UserPrincipal principal) {
        // Validate mandatory fields
        if (request.getDepartment() == null || request.getDepartment().isBlank()) {
            throw new BadRequestException("Department, Expense Title, Expense Date, and Amount are mandatory.");
        }
        if (request.getExpenseTitle() == null || request.getExpenseTitle().isBlank()) {
            throw new BadRequestException("Department, Expense Title, Expense Date, and Amount are mandatory.");
        }
        if (request.getExpenseDate() == null || request.getExpenseDate().isBlank()) {
            throw new BadRequestException("Department, Expense Title, Expense Date, and Amount are mandatory.");
        }
        if (request.getAmount() == null) {
            throw new BadRequestException("Department, Expense Title, Expense Date, and Amount are mandatory.");
        }

        // Validate amount is positive
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be greater than zero.");
        }

        // Validate expense date is not in the future and not older than 7 days
        LocalDate expenseDate = LocalDate.parse(request.getExpenseDate());
        if (expenseDate.isAfter(LocalDate.now())) {
            throw new BadRequestException("Expense date cannot be in the future.");
        }
        if (expenseDate.isBefore(LocalDate.now().minusDays(7))) {
            throw new BadRequestException("Expense date cannot be older than 7 days from today.");
        }

        // Look up the employee user entity
        User employee = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        // Generate unique voucher number
        String voucherNumber = generateVoucherNumber();

        // Determine voucher date (defaults to today)
        LocalDate voucherDate = (request.getVoucherDate() != null && !request.getVoucherDate().isBlank())
                ? LocalDate.parse(request.getVoucherDate())
                : LocalDate.now();

        // Build and save the voucher entity
        Voucher voucher = new Voucher();
        voucher.setVoucherNumber(voucherNumber);
        voucher.setEmployee(employee);
        voucher.setEmployeeName(principal.getName());
        voucher.setEmployeeCode(request.getEmployeeCode());
        voucher.setDepartment(request.getDepartment());
        voucher.setVoucherDate(voucherDate);
        voucher.setExpenseDate(expenseDate);
        voucher.setExpenseTitle(request.getExpenseTitle());
        voucher.setExpenseCategory(request.getExpenseCategory() != null ? request.getExpenseCategory() : "General");
        voucher.setExpenseDescription(request.getExpenseDescription() != null ? request.getExpenseDescription() : "");
        voucher.setAmount(request.getAmount());
        voucher.setStatus(VoucherStatus.DRAFT);

        // Store mandatory proof document
        if (proofDocument == null || proofDocument.isEmpty()) {
            throw new BadRequestException("Proof document (bill/voucher image or PDF) is mandatory.");
        }
        String proofFilename = fileStorageService.storeProofDocument(proofDocument);
        voucher.setProofDocument(proofFilename);

        Voucher saved = voucherRepository.save(voucher);
        return VoucherResponse.fromEntity(saved);
    }

    // -----------------------------------------------------------------------
    // READ — GET /api/vouchers (role-scoped, filtered, sorted, paginated)
    // -----------------------------------------------------------------------

    /**
     * Lists vouchers with role-based scoping, filtering, sorting, and pagination.
     * <p>
     * Employees see only their own vouchers. Directors and Accounts see all.
     * Supports query filters: voucherNumber, employeeName, department, category,
     * status, dateFrom, dateTo, amountMin, amountMax.
     * Supports sorting by: voucherNumber, employeeName, department, expenseDate,
     * amount, status, createdAt.
     *
     * @param principal    the authenticated user's identity
     * @param params       query parameters for filtering/sorting/pagination
     * @return paginated list of voucher response DTOs
     */
    @Transactional(readOnly = true)
    public PaginatedResponse<VoucherResponse> listVouchers(UserPrincipal principal,
                                                           Map<String, String> params) {
        // Build dynamic filter specification from query parameters
        Specification<Voucher> spec = buildSpecification(principal, params);

        // Parse pagination parameters
        int page = Math.max(1, parseIntParam(params.get("page"), 1));
        int limit = Math.max(1, parseIntParam(params.get("limit"), 10));

        // Build sort order
        Sort sort = buildSort(params.get("sortBy"), params.get("sortDir"));

        // Execute paginated query
        Pageable pageable = PageRequest.of(page - 1, limit, sort);
        Page<Voucher> resultPage = voucherRepository.findAll(spec, pageable);

        // Convert entities to response DTOs
        List<VoucherResponse> data = resultPage.getContent().stream()
                .map(VoucherResponse::fromEntity)
                .collect(Collectors.toList());

        // Build paginated response matching the Node.js format
        PaginatedResponse.Meta meta = new PaginatedResponse.Meta(
                resultPage.getTotalElements(),
                page,
                limit,
                resultPage.getTotalPages() == 0 ? 1 : resultPage.getTotalPages()
        );
        return new PaginatedResponse<>(data, meta);
    }

    // -----------------------------------------------------------------------
    // READ — GET /api/vouchers/dashboard/summary (role-aware KPIs)
    // -----------------------------------------------------------------------

    /**
     * Computes dashboard summary KPIs based on the authenticated user's role.
     * <p>
     * Returns different data shapes for each role:
     * <ul>
     *   <li>Employee: own voucher counts by status + total amount claimed</li>
     *   <li>Director: pending count, today's approved/rejected, recent activity</li>
     *   <li>Accounts: total counts by status, total approved amount, recent approved</li>
     * </ul>
     *
     * @param principal the authenticated user's identity
     * @return a Map representing the role-specific dashboard JSON
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardSummary(UserPrincipal principal) {
        String role = principal.getRole();
        Map<String, Object> summary = new LinkedHashMap<>();

        if ("EMPLOYEE".equals(role)) {
            // Employee dashboard — scoped to their own vouchers
            Long empId = principal.getId();
            List<Voucher> rows = voucherRepository.findByEmployeeId(empId);

            summary.put("totalVouchers", rows.size());
            summary.put("draftVouchers", rows.stream().filter(v -> v.getStatus() == VoucherStatus.DRAFT).count());
            summary.put("pendingApproval", rows.stream().filter(v -> v.getStatus() == VoucherStatus.SUBMITTED).count());
            summary.put("approvedVouchers", rows.stream().filter(v -> v.getStatus() == VoucherStatus.APPROVED).count());
            summary.put("rejectedVouchers", rows.stream().filter(v -> v.getStatus() == VoucherStatus.REJECTED).count());
            summary.put("totalAmountClaimed", rows.stream()
                    .map(Voucher::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));

        } else if ("DIRECTOR".equals(role)) {
            // Director dashboard — sees all vouchers
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

            summary.put("pendingApprovalCount", voucherRepository.countByStatus(VoucherStatus.SUBMITTED));
            summary.put("approvedToday", voucherRepository.countByStatusAndUpdatedAtBetween(
                    VoucherStatus.APPROVED, startOfDay, endOfDay));
            summary.put("rejectedToday", voucherRepository.countByStatusAndUpdatedAtBetween(
                    VoucherStatus.REJECTED, startOfDay, endOfDay));
            summary.put("totalPendingAmount", voucherRepository.sumAmountByStatus(VoucherStatus.SUBMITTED));

            // Recent activity — last 5 updated vouchers (excluding DRAFTs)
            Page<Voucher> recentPage = voucherRepository.findByStatusNotOrderByUpdatedAtDesc(VoucherStatus.DRAFT, PageRequest.of(0, 5));
            summary.put("recentActivity", recentPage.getContent().stream()
                    .map(VoucherResponse::fromEntity)
                    .collect(Collectors.toList()));

        } else if ("ACCOUNTS".equals(role)) {
            // Accounts dashboard — sees all vouchers (excluding DRAFTs)
            List<Voucher> all = voucherRepository.findByStatusNot(VoucherStatus.DRAFT);

            summary.put("totalVouchers", all.size());
            summary.put("pendingApproval", all.stream().filter(v -> v.getStatus() == VoucherStatus.SUBMITTED).count());
            summary.put("approvedVouchers", all.stream().filter(v -> v.getStatus() == VoucherStatus.APPROVED).count());
            summary.put("rejectedVouchers", all.stream().filter(v -> v.getStatus() == VoucherStatus.REJECTED).count());
            summary.put("totalApprovedExpenseAmount", all.stream()
                    .filter(v -> v.getStatus() == VoucherStatus.APPROVED)
                    .map(Voucher::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));

            // Recent approved vouchers — last 5
            Page<Voucher> recentApproved = voucherRepository.findByStatusOrderByUpdatedAtDesc(
                    VoucherStatus.APPROVED, PageRequest.of(0, 5));
            summary.put("recentApprovedVouchers", recentApproved.getContent().stream()
                    .map(VoucherResponse::fromEntity)
                    .collect(Collectors.toList()));
        }

        return summary;
    }

    // -----------------------------------------------------------------------
    // READ — GET /api/vouchers/{id} (role-scoped)
    // -----------------------------------------------------------------------

    /**
     * Retrieves a single voucher by ID with role-based access control.
     * <p>
     * Employees can only view their own vouchers. Directors and Accounts
     * can view any voucher.
     *
     * @param id        the voucher ID
     * @param principal the authenticated user's identity
     * @return the voucher as a response DTO
     * @throws ResourceNotFoundException if the voucher doesn't exist
     * @throws AccessDeniedException     if an employee tries to view another's voucher
     */
    @Transactional(readOnly = true)
    public VoucherResponse getVoucher(Long id, UserPrincipal principal) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found."));

        // Employees can only view their own vouchers
        if ("EMPLOYEE".equals(principal.getRole())
                && !voucher.getEmployee().getId().equals(principal.getId())) {
            throw new AccessDeniedException("You do not have permission to view this voucher.");
        }

        // Non-employees cannot view DRAFT vouchers
        if (!"EMPLOYEE".equals(principal.getRole()) && voucher.getStatus() == VoucherStatus.DRAFT) {
            throw new AccessDeniedException("Draft vouchers are only visible to the creator.");
        }

        return VoucherResponse.fromEntity(voucher);
    }

    // -----------------------------------------------------------------------
    // UPDATE — PUT /api/vouchers/{id} (Employee, owner, DRAFT only)
    // -----------------------------------------------------------------------

    /**
     * Updates a DRAFT voucher. Only the owning employee can edit their own
     * draft vouchers. All fields are optional — only provided fields override.
     *
     * @param id        the voucher ID
     * @param request   the update request body
     * @param principal the authenticated employee's identity
     * @return the updated voucher as a response DTO
     * @throws ResourceNotFoundException if the voucher doesn't exist
     * @throws AccessDeniedException     if the user doesn't own the voucher
     * @throws InvalidStateException     if the voucher is not in DRAFT status
     */
    @Transactional
    public VoucherResponse updateVoucher(Long id, VoucherUpdateRequest request,
                                         MultipartFile proofDocument,
                                         UserPrincipal principal) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found."));

        // Only the owner can edit
        if (!voucher.getEmployee().getId().equals(principal.getId())) {
            throw new AccessDeniedException("You can only edit your own vouchers.");
        }

        // Only DRAFT vouchers can be edited
        if (voucher.getStatus() != VoucherStatus.DRAFT) {
            throw new InvalidStateException("Only Draft vouchers can be edited.");
        }

        // Apply partial updates — only override fields that are provided
        if (request.getDepartment() != null) voucher.setDepartment(request.getDepartment());
        if (request.getVoucherDate() != null) voucher.setVoucherDate(LocalDate.parse(request.getVoucherDate()));
        if (request.getExpenseTitle() != null) voucher.setExpenseTitle(request.getExpenseTitle());
        if (request.getExpenseCategory() != null) voucher.setExpenseCategory(request.getExpenseCategory());
        if (request.getExpenseDescription() != null) voucher.setExpenseDescription(request.getExpenseDescription());
        if (request.getEmployeeCode() != null) voucher.setEmployeeCode(request.getEmployeeCode());

        // Validate and apply amount if provided
        if (request.getAmount() != null) {
            if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Amount must be greater than zero.");
            }
            voucher.setAmount(request.getAmount());
        }

        // Validate and apply expense date if provided
        LocalDate effectiveExpenseDate = request.getExpenseDate() != null
                ? LocalDate.parse(request.getExpenseDate())
                : voucher.getExpenseDate();
        if (effectiveExpenseDate.isAfter(LocalDate.now())) {
            throw new BadRequestException("Expense date cannot be in the future.");
        }
        if (effectiveExpenseDate.isBefore(LocalDate.now().minusDays(7))) {
            throw new BadRequestException("Expense date cannot be older than 7 days from today.");
        }
        if (request.getExpenseDate() != null) {
            voucher.setExpenseDate(effectiveExpenseDate);
        }

        // Handle proof document replacement if a new one is uploaded
        if (proofDocument != null && !proofDocument.isEmpty()) {
            // Clean up old proof document
            if (voucher.getProofDocument() != null) {
                fileStorageService.deleteProofDocument(voucher.getProofDocument());
            }
            String proofFilename = fileStorageService.storeProofDocument(proofDocument);
            voucher.setProofDocument(proofFilename);
        }

        Voucher updated = voucherRepository.save(voucher);
        return VoucherResponse.fromEntity(updated);
    }

    // -----------------------------------------------------------------------
    // DELETE — DELETE /api/vouchers/{id} (Employee, owner, DRAFT only)
    // -----------------------------------------------------------------------

    /**
     * Deletes a DRAFT voucher. Only the owning employee can delete their own
     * draft vouchers. Also cleans up any associated signature file.
     *
     * @param id        the voucher ID
     * @param principal the authenticated employee's identity
     * @throws ResourceNotFoundException if the voucher doesn't exist
     * @throws AccessDeniedException     if the user doesn't own the voucher
     * @throws InvalidStateException     if the voucher is not in DRAFT status
     */
    @Transactional
    public void deleteVoucher(Long id, UserPrincipal principal) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found."));

        if (!voucher.getEmployee().getId().equals(principal.getId())) {
            throw new AccessDeniedException("You can only delete your own vouchers.");
        }

        if (voucher.getStatus() != VoucherStatus.DRAFT) {
            throw new InvalidStateException("Only Draft vouchers can be deleted.");
        }

        // Clean up signature file if it exists
        if (voucher.getEmployeeSignature() != null) {
            fileStorageService.deleteFile(voucher.getEmployeeSignature());
        }

        // Clean up proof document file if it exists
        if (voucher.getProofDocument() != null) {
            fileStorageService.deleteProofDocument(voucher.getProofDocument());
        }

        voucherRepository.delete(voucher);
    }

    // -----------------------------------------------------------------------
    // SUBMIT — POST /api/vouchers/{id}/submit (Employee, owner, DRAFT → SUBMITTED)
    // -----------------------------------------------------------------------

    /**
     * Submits a DRAFT voucher for director approval.
     * <p>
     * Transitions the voucher from DRAFT → SUBMITTED. Requires an employee
     * signature image (uploaded as a multipart file or previously attached).
     *
     * @param id            the voucher ID
     * @param signatureFile the employee's signature image (optional if already attached)
     * @param principal     the authenticated employee's identity
     * @return the updated voucher as a response DTO
     */
    @Transactional
    public VoucherResponse submitVoucher(Long id, MultipartFile signatureFile,
                                         UserPrincipal principal) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found."));

        if (!voucher.getEmployee().getId().equals(principal.getId())) {
            throw new AccessDeniedException("You can only submit your own vouchers.");
        }

        if (voucher.getStatus() != VoucherStatus.DRAFT) {
            throw new InvalidStateException("Only Draft vouchers can be submitted.");
        }

        // Store signature file if provided, otherwise use existing
        String signatureFilename = voucher.getEmployeeSignature();
        if (signatureFile != null && !signatureFile.isEmpty()) {
            signatureFilename = fileStorageService.storeFile(signatureFile);
        }

        // Employee signature is mandatory for submission
        if (signatureFilename == null || signatureFilename.isBlank()) {
            throw new BadRequestException("Employee signature is mandatory before submission.");
        }

        // Transition: DRAFT → SUBMITTED
        voucher.setStatus(VoucherStatus.SUBMITTED);
        voucher.setEmployeeSignature(signatureFilename);

        Voucher updated = voucherRepository.save(voucher);
        return VoucherResponse.fromEntity(updated);
    }

    // -----------------------------------------------------------------------
    // APPROVE — POST /api/vouchers/{id}/approve (Director, SUBMITTED → APPROVED)
    // -----------------------------------------------------------------------

    /**
     * Approves a SUBMITTED voucher. Only a Director can approve, and a
     * director signature image is mandatory.
     *
     * @param id            the voucher ID
     * @param signatureFile the director's signature image (mandatory)
     * @param principal     the authenticated director's identity
     * @return the updated voucher as a response DTO
     */
    @Transactional
    public VoucherResponse approveVoucher(Long id, MultipartFile signatureFile,
                                           UserPrincipal principal) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found."));

        if (voucher.getStatus() != VoucherStatus.SUBMITTED) {
            throw new InvalidStateException("Only vouchers pending approval can be approved.");
        }

        // Director signature is mandatory for approval
        if (signatureFile == null || signatureFile.isEmpty()) {
            throw new BadRequestException("Director signature is mandatory before approval.");
        }

        String signatureFilename = fileStorageService.storeFile(signatureFile);

        // Look up the approver (director) user entity
        User approver = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        // Transition: SUBMITTED → APPROVED
        voucher.setStatus(VoucherStatus.APPROVED);
        voucher.setDirectorSignature(signatureFilename);
        voucher.setApprovalDate(LocalDateTime.now());
        voucher.setApprover(approver);
        voucher.setRejectionReason(null); // Clear any previous rejection reason

        Voucher updated = voucherRepository.save(voucher);
        return VoucherResponse.fromEntity(updated);
    }

    // -----------------------------------------------------------------------
    // REJECT — POST /api/vouchers/{id}/reject (Director, SUBMITTED → REJECTED)
    // -----------------------------------------------------------------------

    /**
     * Rejects a SUBMITTED voucher. Only a Director can reject, and a
     * non-empty rejection reason is mandatory.
     *
     * @param id        the voucher ID
     * @param request   the rejection request containing the reason
     * @param principal the authenticated director's identity
     * @return the updated voucher as a response DTO
     */
    @Transactional
    public VoucherResponse rejectVoucher(Long id, RejectRequest request,
                                          UserPrincipal principal) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found."));

        if (voucher.getStatus() != VoucherStatus.SUBMITTED) {
            throw new InvalidStateException("Only vouchers pending approval can be rejected.");
        }

        // Rejection reason is mandatory
        if (request.getRejectionReason() == null || request.getRejectionReason().trim().isEmpty()) {
            throw new BadRequestException("Rejection reason is mandatory when rejecting a voucher.");
        }

        // Look up the approver (director) user entity
        User approver = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        // Transition: SUBMITTED → REJECTED
        voucher.setStatus(VoucherStatus.REJECTED);
        voucher.setRejectionReason(request.getRejectionReason().trim());
        voucher.setApprover(approver);

        Voucher updated = voucherRepository.save(voucher);
        return VoucherResponse.fromEntity(updated);
    }

    // -----------------------------------------------------------------------
    // Helper: Generate unique voucher numbers (EV-{year}-{seq})
    // -----------------------------------------------------------------------

    /**
     * Generates a unique voucher number for the current year.
     * <p>
     * Format: "EV-{year}-{4-digit-sequence}" (e.g., "EV-2026-0001").
     * The sequence is stored in the {@code voucher_counter} table and
     * incremented atomically within a transaction to prevent collisions.
     *
     * @return a unique voucher number string
     */
    private String generateVoucherNumber() {
        int year = LocalDate.now().getYear();

        // Find or create the counter for the current year
        Optional<VoucherCounter> counterOpt = counterRepository.findByYear(year);
        int seq;
        if (counterOpt.isPresent()) {
            VoucherCounter counter = counterOpt.get();
            seq = counter.getSeq() + 1;
            counter.setSeq(seq);
            counterRepository.save(counter);
        } else {
            seq = 1;
            counterRepository.save(new VoucherCounter(year, seq));
        }

        return String.format("EV-%d-%04d", year, seq);
    }

    // -----------------------------------------------------------------------
    // Helper: Build JPA Specification for dynamic filtering
    // -----------------------------------------------------------------------

    /**
     * Builds a JPA {@link Specification} from the request query parameters.
     * <p>
     * Replaces the {@code buildFilters()} function from the Node.js backend.
     * Each non-null query parameter adds a WHERE clause predicate.
     *
     * @param principal the authenticated user (for employee scoping)
     * @param params    the query parameters map
     * @return a composite specification combining all filters
     */
    private Specification<Voucher> buildSpecification(UserPrincipal principal,
                                                       Map<String, String> params) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Employee scope — only see own vouchers
            if ("EMPLOYEE".equals(principal.getRole())) {
                predicates.add(cb.equal(root.get("employee").get("id"), principal.getId()));
            } else {
                // Non-employees cannot see DRAFT vouchers
                predicates.add(cb.notEqual(root.get("status"), VoucherStatus.DRAFT));
            }

            // Filter by voucher number (partial match)
            String voucherNumber = params.get("voucherNumber");
            if (voucherNumber != null && !voucherNumber.isBlank()) {
                predicates.add(cb.like(root.get("voucherNumber"), "%" + voucherNumber + "%"));
            }

            // Filter by employee name (partial match)
            String employeeName = params.get("employeeName");
            if (employeeName != null && !employeeName.isBlank()) {
                predicates.add(cb.like(root.get("employeeName"), "%" + employeeName + "%"));
            }

            // Filter by department (partial match)
            String department = params.get("department");
            if (department != null && !department.isBlank()) {
                predicates.add(cb.like(root.get("department"), "%" + department + "%"));
            }

            // Filter by category (partial match)
            String category = params.get("category");
            if (category != null && !category.isBlank()) {
                predicates.add(cb.like(root.get("expenseCategory"), "%" + category + "%"));
            }

            // Filter by exact status
            String status = params.get("status");
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), VoucherStatus.valueOf(status)));
            }

            // Filter by date range
            String dateFrom = params.get("dateFrom");
            if (dateFrom != null && !dateFrom.isBlank()) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("expenseDate"), LocalDate.parse(dateFrom)));
            }
            String dateTo = params.get("dateTo");
            if (dateTo != null && !dateTo.isBlank()) {
                predicates.add(cb.lessThanOrEqualTo(root.get("expenseDate"), LocalDate.parse(dateTo)));
            }

            // Filter by amount range
            String amountMin = params.get("amountMin");
            if (amountMin != null && !amountMin.isBlank()) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), new BigDecimal(amountMin)));
            }
            String amountMax = params.get("amountMax");
            if (amountMax != null && !amountMax.isBlank()) {
                predicates.add(cb.lessThanOrEqualTo(root.get("amount"), new BigDecimal(amountMax)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Builds a {@link Sort} object from the sort query parameters.
     * <p>
     * Only allows sorting by whitelisted columns to prevent SQL injection.
     * Defaults to sorting by {@code createdAt DESC}.
     *
     * @param sortBy  the column name to sort by
     * @param sortDir the sort direction ("asc" or "desc")
     * @return a Sort object for the JPA query
     */
    private Sort buildSort(String sortBy, String sortDir) {
        // Map frontend column names (snake_case) to JPA field names (camelCase)
        Map<String, String> columnMap = Map.of(
                "voucher_number", "voucherNumber",
                "employee_name", "employeeName",
                "expense_date", "expenseDate",
                "created_at", "createdAt"
        );

        String column = sortBy != null ? columnMap.getOrDefault(sortBy, sortBy) : "createdAt";
        if (!ALLOWED_SORT_COLUMNS.contains(column)) {
            column = "createdAt";
        }

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, column);
    }

    /**
     * Safely parses an integer from a string, returning a default value on failure.
     *
     * @param value        the string to parse
     * @param defaultValue the default value if parsing fails
     * @return the parsed integer or the default value
     */
    private int parseIntParam(String value, int defaultValue) {
        if (value == null || value.isBlank()) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
