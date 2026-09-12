package com.capgemini.apartment_maintenance.service.impl;

import com.capgemini.apartment_maintenance.dto.BuildingRequestDTO;
import com.capgemini.apartment_maintenance.dto.BuildingResponseDTO;
import com.capgemini.apartment_maintenance.entity.Building;
import com.capgemini.apartment_maintenance.exception.InvalidOperationException;
import com.capgemini.apartment_maintenance.exception.ResourceNotFoundException;
import com.capgemini.apartment_maintenance.repository.BuildingRepository;
import com.capgemini.apartment_maintenance.service.BuildingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BuildingServiceImpl implements BuildingService {

    private final BuildingRepository buildingRepository;

    @Override
    @Transactional
    public BuildingResponseDTO createBuilding(BuildingRequestDTO dto) {
        Building building = Building.builder()
                .name(dto.name())
                .address(dto.address())
                .totalFlats(dto.totalFlats())
                .build();
        Building saved = buildingRepository.save(building);
        log.info("Created building '{}' with id {}", saved.getName(), saved.getBuildingId());
        return toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BuildingResponseDTO> getAllBuildings() {
        return buildingRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BuildingResponseDTO getBuildingById(Integer id) {
        return toResponseDTO(findBuildingOrThrow(id));
    }

    @Override
    @Transactional
    public BuildingResponseDTO updateBuilding(Integer id, BuildingRequestDTO dto) {
        Building building = findBuildingOrThrow(id);
        building.setName(dto.name());
        building.setAddress(dto.address());
        building.setTotalFlats(dto.totalFlats());
        log.info("Updated building id {}", id);
        return toResponseDTO(building);
    }

    @Override
    @Transactional
    public void deleteBuilding(Integer id) {
        Building building = findBuildingOrThrow(id);
        if (!building.getResidents().isEmpty()) {
            throw new InvalidOperationException(
                    "Cannot delete building id " + id + " — it still has " +
                    building.getResidents().size() + " resident(s). Remove residents first.");
        }
        buildingRepository.delete(building);
        log.info("Deleted building id {}", id);
    }

    private Building findBuildingOrThrow(Integer id) {
        return buildingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + id));
    }

    private BuildingResponseDTO toResponseDTO(Building b) {
        return new BuildingResponseDTO(
                b.getBuildingId(), b.getName(), b.getAddress(),
                b.getTotalFlats(), b.getResidents().size());
    }
}