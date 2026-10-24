package com.capgemini.apartment_maintenance.service;

import com.capgemini.apartment_maintenance.dto.ResidentRequestDTO;
import com.capgemini.apartment_maintenance.dto.ResidentResponseDTO;
import java.util.List;

public interface ResidentService {
    ResidentResponseDTO createResident(ResidentRequestDTO dto);
    List<ResidentResponseDTO> getAllResidents();
    ResidentResponseDTO getResidentById(Integer id);
    List<ResidentResponseDTO> getResidentsByBuilding(Integer buildingId);
    ResidentResponseDTO updateResident(Integer id, ResidentRequestDTO dto);
    void deleteResident(Integer id);
}