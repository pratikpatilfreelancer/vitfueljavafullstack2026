package com.capgemini.apartment_maintenance.service.impl;

import com.capgemini.apartment_maintenance.dto.*;
import com.capgemini.apartment_maintenance.entity.*;
import com.capgemini.apartment_maintenance.exception.InvalidOperationException;
import com.capgemini.apartment_maintenance.exception.ResourceNotFoundException;
import com.capgemini.apartment_maintenance.repository.*;
import com.capgemini.apartment_maintenance.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ResidentRepository residentRepository;
    private final StaffRepository staffRepository;
    private final ComplaintCategoryRepository categoryRepository;

    // defines which status transitions are legal — prevents e.g. OPEN -> CLOSED skipping steps
    private static final Map<ComplaintStatus, Set<ComplaintStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(ComplaintStatus.class);
    static {
        ALLOWED_TRANSITIONS.put(ComplaintStatus.OPEN, EnumSet.of(ComplaintStatus.IN_PROGRESS));
        ALLOWED_TRANSITIONS.put(ComplaintStatus.IN_PROGRESS, EnumSet.of(ComplaintStatus.RESOLVED));
        ALLOWED_TRANSITIONS.put(ComplaintStatus.RESOLVED, EnumSet.of(ComplaintStatus.CLOSED));
        ALLOWED_TRANSITIONS.put(ComplaintStatus.CLOSED, EnumSet.noneOf(ComplaintStatus.class));
    }

    @Override
    @Transactional
    public ComplaintResponseDTO createComplaint(ComplaintRequestDTO dto) {
        Resident resident = residentRepository.findById(dto.residentId())
                .orElseThrow(() -> new ResourceNotFoundException("Resident not found with id: " + dto.residentId()));
        ComplaintCategory category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.categoryId()));

        Complaint complaint = Complaint.builder()
                .description(dto.description())
                .resident(resident)
                .category(category)
                .status(ComplaintStatus.OPEN) // always starts OPEN — never trust client-supplied status at creation
                .build();

        Complaint saved = complaintRepository.save(complaint);
        log.info("Resident {} raised complaint {} in category '{}'", resident.getResidentId(), saved.getComplaintId(), category.getCategoryName());
        return toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintResponseDTO> getAllComplaints() {
        return complaintRepository.findAll().stream().map(this::toResponseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ComplaintResponseDTO getComplaintById(Integer id) {
        return toResponseDTO(findComplaintOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintResponseDTO> getComplaintsByResident(Integer residentId) {
        return complaintRepository.findByResident_ResidentIdOrderByCreatedDateDesc(residentId)
                .stream().map(this::toResponseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintResponseDTO> getComplaintsByStaff(Integer staffId) {
        return complaintRepository.findByStaff_StaffId(staffId)
                .stream().map(this::toResponseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintResponseDTO> getComplaintsByStatus(ComplaintStatus status) {
        return complaintRepository.findByStatus(status).stream().map(this::toResponseDTO).toList();
    }

    @Override
    @Transactional
    public ComplaintResponseDTO assignStaff(Integer complaintId, Integer staffId) {
        Complaint complaint = findComplaintOrThrow(complaintId);
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + staffId));

        if (complaint.getStatus() == ComplaintStatus.CLOSED || complaint.getStatus() == ComplaintStatus.RESOLVED) {
            throw new InvalidOperationException("Cannot assign staff to a complaint that is already " + complaint.getStatus());
        }

        complaint.setStaff(staff);
        if (complaint.getStatus() == ComplaintStatus.OPEN) {
            complaint.setStatus(ComplaintStatus.IN_PROGRESS); // assigning staff naturally moves it forward
        }
        log.info("Assigned staff {} to complaint {}", staffId, complaintId);
        return toResponseDTO(complaint);
    }

    @Override
    @Transactional
    public ComplaintResponseDTO updateStatus(Integer complaintId, ComplaintStatusUpdateDTO dto) {
        Complaint complaint = findComplaintOrThrow(complaintId);
        ComplaintStatus current = complaint.getStatus();
        ComplaintStatus target = dto.status();

        if (!ALLOWED_TRANSITIONS.get(current).contains(target)) {
            throw new InvalidOperationException(
                    "Invalid status transition: " + current + " -> " + target);
        }

        // core business rule: can't resolve a complaint with no staff assigned
        if (target == ComplaintStatus.RESOLVED && complaint.getStaff() == null) {
            throw new InvalidOperationException(
                    "Cannot resolve complaint id " + complaintId + " — no staff is assigned yet.");
        }

        complaint.setStatus(target);
        if (target == ComplaintStatus.RESOLVED) {
            complaint.setResolvedDate(LocalDate.now()); // system-controlled, never client-supplied
        }

        log.info("Complaint {} status changed {} -> {}", complaintId, current, target);
        return toResponseDTO(complaint);
    }

    @Override
    @Transactional
    public void deleteComplaint(Integer id) {
        Complaint complaint = findComplaintOrThrow(id);
        complaintRepository.delete(complaint);
        log.info("Deleted complaint id {}", id);
    }

    private Complaint findComplaintOrThrow(Integer id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));
    }

    private ComplaintResponseDTO toResponseDTO(Complaint c) {
        return new ComplaintResponseDTO(
                c.getComplaintId(),
                c.getDescription(),
                c.getStatus(),
                c.getCreatedDate(),
                c.getResolvedDate(),
                c.getResident().getName(),
                c.getStaff() != null ? c.getStaff().getName() : null,
                c.getCategory().getCategoryName()
        );
    }
}