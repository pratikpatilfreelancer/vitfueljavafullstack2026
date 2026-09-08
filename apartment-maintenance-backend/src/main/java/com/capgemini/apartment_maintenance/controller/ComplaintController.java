package com.capgemini.apartment_maintenance.controller;

import com.capgemini.apartment_maintenance.dto.ComplaintRequestDTO;
import com.capgemini.apartment_maintenance.dto.ComplaintResponseDTO;
import com.capgemini.apartment_maintenance.dto.ComplaintStatusUpdateDTO;
import com.capgemini.apartment_maintenance.entity.ComplaintStatus;
import com.capgemini.apartment_maintenance.service.ComplaintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping
    public ResponseEntity<ComplaintResponseDTO> createComplaint(@Valid @RequestBody ComplaintRequestDTO dto) {
        ComplaintResponseDTO created = complaintService.createComplaint(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ComplaintResponseDTO>> getAllComplaints() {
        return ResponseEntity.ok(complaintService.getAllComplaints());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComplaintResponseDTO> getComplaintById(@PathVariable Integer id) {
        return ResponseEntity.ok(complaintService.getComplaintById(id));
    }

    // filter by resident, e.g. GET /api/complaints/resident/5
    @GetMapping("/resident/{residentId}")
    public ResponseEntity<List<ComplaintResponseDTO>> getComplaintsByResident(@PathVariable Integer residentId) {
        return ResponseEntity.ok(complaintService.getComplaintsByResident(residentId));
    }

    // filter by staff, e.g. GET /api/complaints/staff/3
    @GetMapping("/staff/{staffId}")
    public ResponseEntity<List<ComplaintResponseDTO>> getComplaintsByStaff(@PathVariable Integer staffId) {
        return ResponseEntity.ok(complaintService.getComplaintsByStaff(staffId));
    }

    // filter by status, e.g. GET /api/complaints/status/OPEN
    // ComplaintStatus is an enum — Spring binds the path segment to it automatically (case-sensitive match)
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ComplaintResponseDTO>> getComplaintsByStatus(@PathVariable ComplaintStatus status) {
        return ResponseEntity.ok(complaintService.getComplaintsByStatus(status));
    }

    // assign a staff member to a complaint, e.g. PUT /api/complaints/12/assign/3
    @PutMapping("/{complaintId}/assign/{staffId}")
    public ResponseEntity<ComplaintResponseDTO> assignStaff(
            @PathVariable Integer complaintId,
            @PathVariable Integer staffId) {
        return ResponseEntity.ok(complaintService.assignStaff(complaintId, staffId));
    }

    // change status, e.g. PUT /api/complaints/12/status  body: { "status": "RESOLVED" }
    // kept separate from a full update since only status transitions are business-rule-checked
    @PutMapping("/{id}/status")
    public ResponseEntity<ComplaintResponseDTO> updateStatus(
            @PathVariable Integer id,
            @Valid @RequestBody ComplaintStatusUpdateDTO dto) {
        return ResponseEntity.ok(complaintService.updateStatus(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComplaint(@PathVariable Integer id) {
        complaintService.deleteComplaint(id);
        return ResponseEntity.noContent().build();
    }
}