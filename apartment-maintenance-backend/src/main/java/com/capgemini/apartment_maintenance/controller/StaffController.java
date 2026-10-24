package com.capgemini.apartment_maintenance.controller;

import com.capgemini.apartment_maintenance.dto.StaffRequestDTO;
import com.capgemini.apartment_maintenance.dto.StaffResponseDTO;
import com.capgemini.apartment_maintenance.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @PostMapping
    public ResponseEntity<StaffResponseDTO> createStaff(@Valid @RequestBody StaffRequestDTO dto) {
        StaffResponseDTO created = staffService.createStaff(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<StaffResponseDTO>> getAllStaff() {
        return ResponseEntity.ok(staffService.getAllStaff());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffResponseDTO> getStaffById(@PathVariable Integer id) {
        return ResponseEntity.ok(staffService.getStaffById(id));
    }

    // filter staff by specialization, e.g. GET /api/staff/specialization/Plumbing
    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<StaffResponseDTO>> getStaffBySpecialization(@PathVariable String specialization) {
        return ResponseEntity.ok(staffService.getStaffBySpecialization(specialization));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StaffResponseDTO> updateStaff(
            @PathVariable Integer id,
            @Valid @RequestBody StaffRequestDTO dto) {
        return ResponseEntity.ok(staffService.updateStaff(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable Integer id) {
        staffService.deleteStaff(id);
        return ResponseEntity.noContent().build();
    }
}