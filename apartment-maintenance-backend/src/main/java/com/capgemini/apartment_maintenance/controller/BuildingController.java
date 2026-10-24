package com.capgemini.apartment_maintenance.controller;

import com.capgemini.apartment_maintenance.dto.BuildingRequestDTO;
import com.capgemini.apartment_maintenance.dto.BuildingResponseDTO;
import com.capgemini.apartment_maintenance.service.BuildingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buildings")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @PostMapping
    public ResponseEntity<BuildingResponseDTO> createBuilding(@Valid @RequestBody BuildingRequestDTO dto) {
        BuildingResponseDTO created = buildingService.createBuilding(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BuildingResponseDTO>> getAllBuildings() {
        return ResponseEntity.ok(buildingService.getAllBuildings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuildingResponseDTO> getBuildingById(@PathVariable Integer id) {
        return ResponseEntity.ok(buildingService.getBuildingById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BuildingResponseDTO> updateBuilding(
            @PathVariable Integer id,
            @Valid @RequestBody BuildingRequestDTO dto) {
        return ResponseEntity.ok(buildingService.updateBuilding(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBuilding(@PathVariable Integer id) {
        buildingService.deleteBuilding(id);
        return ResponseEntity.noContent().build();
    }
}