package com.capgemini.apartment_maintenance.controller;

import com.capgemini.apartment_maintenance.dto.ResidentRequestDTO;
import com.capgemini.apartment_maintenance.dto.ResidentResponseDTO;
import com.capgemini.apartment_maintenance.service.ResidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/residents")
@RequiredArgsConstructor
public class ResidentController {

    private final ResidentService residentService;

    @PostMapping
    public ResponseEntity<ResidentResponseDTO> createResident(@Valid @RequestBody ResidentRequestDTO dto) {
        ResidentResponseDTO created = residentService.createResident(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ResidentResponseDTO>> getAllResidents() {
        return ResponseEntity.ok(residentService.getAllResidents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResidentResponseDTO> getResidentById(@PathVariable Integer id) {
        return ResponseEntity.ok(residentService.getResidentById(id));
    }

    // filter residents by building, e.g. GET /api/residents/building/3
    @GetMapping("/building/{buildingId}")
    public ResponseEntity<List<ResidentResponseDTO>> getResidentsByBuilding(@PathVariable Integer buildingId) {
        return ResponseEntity.ok(residentService.getResidentsByBuilding(buildingId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResidentResponseDTO> updateResident(
            @PathVariable Integer id,
            @Valid @RequestBody ResidentRequestDTO dto) {
        return ResponseEntity.ok(residentService.updateResident(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResident(@PathVariable Integer id) {
        residentService.deleteResident(id);
        return ResponseEntity.noContent().build();
    }
}