package com.capgemini.apartment_maintenance.service.impl;

import com.capgemini.apartment_maintenance.dto.StaffRequestDTO;
import com.capgemini.apartment_maintenance.dto.StaffResponseDTO;
import com.capgemini.apartment_maintenance.entity.Staff;
import com.capgemini.apartment_maintenance.exception.InvalidOperationException;
import com.capgemini.apartment_maintenance.exception.ResourceNotFoundException;
import com.capgemini.apartment_maintenance.repository.StaffRepository;
import com.capgemini.apartment_maintenance.service.StaffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;

    @Override
    @Transactional
    public StaffResponseDTO createStaff(StaffRequestDTO dto) {
        Staff staff = Staff.builder()
                .name(dto.name())
                .designation(dto.designation())
                .specialization(dto.specialization())
                .build();
        Staff saved = staffRepository.save(staff);
        log.info("Created staff '{}' ({}) with id {}", saved.getName(), saved.getSpecialization(), saved.getStaffId());
        return toResponseDTO(saved);
    }

    @Override
    public List<StaffResponseDTO> getAllStaff() {
        return staffRepository.findAll().stream().map(this::toResponseDTO).toList();
    }

    @Override
    public StaffResponseDTO getStaffById(Integer id) {
        return toResponseDTO(findStaffOrThrow(id));
    }

    @Override
    public List<StaffResponseDTO> getStaffBySpecialization(String specialization) {
        return staffRepository.findBySpecialization(specialization).stream().map(this::toResponseDTO).toList();
    }

    @Override
    @Transactional
    public StaffResponseDTO updateStaff(Integer id, StaffRequestDTO dto) {
        Staff staff = findStaffOrThrow(id);
        staff.setName(dto.name());
        staff.setDesignation(dto.designation());
        staff.setSpecialization(dto.specialization());
        log.info("Updated staff id {}", id);
        return toResponseDTO(staff);
    }

    @Override
    @Transactional
    public void deleteStaff(Integer id) {
        Staff staff = findStaffOrThrow(id);
        if (!staff.getAssignedComplaints().isEmpty()) {
            throw new InvalidOperationException(
                    "Cannot delete staff id " + id + " — reassign their active complaints first.");
        }
        staffRepository.delete(staff);
        log.info("Deleted staff id {}", id);
    }

    private Staff findStaffOrThrow(Integer id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + id));
    }

    private StaffResponseDTO toResponseDTO(Staff s) {
        return new StaffResponseDTO(s.getStaffId(), s.getName(), s.getDesignation(), s.getSpecialization());
    }
}