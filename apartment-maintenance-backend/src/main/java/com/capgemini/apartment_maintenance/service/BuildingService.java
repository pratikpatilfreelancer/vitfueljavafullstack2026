package com.capgemini.apartment_maintenance.service;

import com.capgemini.apartment_maintenance.dto.BuildingRequestDTO;
import com.capgemini.apartment_maintenance.dto.BuildingResponseDTO;
import java.util.List;

public interface BuildingService {
    BuildingResponseDTO createBuilding(BuildingRequestDTO dto);
    List<BuildingResponseDTO> getAllBuildings();
    BuildingResponseDTO getBuildingById(Integer id);
    BuildingResponseDTO updateBuilding(Integer id, BuildingRequestDTO dto);
    void deleteBuilding(Integer id);
}