package com.capgemini.apartment_maintenance.service.impl;

import com.capgemini.apartment_maintenance.dto.ResidentRequestDTO;
import com.capgemini.apartment_maintenance.dto.ResidentResponseDTO;
import com.capgemini.apartment_maintenance.entity.Building;
import com.capgemini.apartment_maintenance.entity.Resident;
import com.capgemini.apartment_maintenance.exception.InvalidOperationException;
import com.capgemini.apartment_maintenance.exception.ResourceNotFoundException;
import com.capgemini.apartment_maintenance.repository.BuildingRepository;
import com.capgemini.apartment_maintenance.repository.ResidentRepository;
import com.capgemini.apartment_maintenance.service.ResidentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResidentServiceImpl implements ResidentService {

    private final ResidentRepository residentRepository;
    private final BuildingRepository buildingRepository;

    @Override
    @Transactional
    public ResidentResponseDTO createResident(ResidentRequestDTO dto) {
        residentRepository.findByEmail(dto.email()).ifPresent(r -> {
            throw new InvalidOperationException("A resident with email '" + dto.email() + "' already exists");
        });

        Building building = buildingRepository.findById(dto.buildingId())
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + dto.buildingId()));

        Resident resident = Resident.builder()
                .name(dto.name())
                .email(dto.email())
                .phone(dto.phone())
                .flatNo(dto.flatNo())
                .building(building)
                .build();

        Resident saved = residentRepository.save(resident);
        log.info("Created resident '{}' in building '{}'", saved.getName(), building.getName());
        return toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResidentResponseDTO> getAllResidents() {
        return residentRepository.findAll().stream().map(this::toResponseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResidentResponseDTO getResidentById(Integer id) {
        return toResponseDTO(findResidentOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResidentResponseDTO> getResidentsByBuilding(Integer buildingId) {
        return residentRepository.findByBuilding_BuildingId(buildingId).stream().map(this::toResponseDTO).toList();
    }

    @Override
    @Transactional
    public ResidentResponseDTO updateResident(Integer id, ResidentRequestDTO dto) {
        Resident resident = findResidentOrThrow(id);

        // if building changed, re-fetch and validate the new one
        if (!resident.getBuilding().getBuildingId().equals(dto.buildingId())) {
            Building newBuilding = buildingRepository.findById(dto.buildingId())
                    .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + dto.buildingId()));
            resident.setBuilding(newBuilding);
        }

        resident.setName(dto.name());
        resident.setEmail(dto.email());
        resident.setPhone(dto.phone());
        resident.setFlatNo(dto.flatNo());
        log.info("Updated resident id {}", id);
        return toResponseDTO(resident);
    }

    @Override
    @Transactional
    public void deleteResident(Integer id) {
        Resident resident = findResidentOrThrow(id);
        residentRepository.delete(resident); // cascade removes their complaints — intentional per entity design
        log.info("Deleted resident id {}", id);
    }

    private Resident findResidentOrThrow(Integer id) {
        return residentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resident not found with id: " + id));
    }

    private ResidentResponseDTO toResponseDTO(Resident r) {
        return new ResidentResponseDTO(
                r.getResidentId(), r.getName(), r.getEmail(), r.getPhone(),
                r.getFlatNo(), r.getBuilding().getName());
    }
}