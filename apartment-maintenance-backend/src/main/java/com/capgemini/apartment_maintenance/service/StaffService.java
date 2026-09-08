package com.capgemini.apartment_maintenance.service;

import com.capgemini.apartment_maintenance.dto.StaffRequestDTO;
import com.capgemini.apartment_maintenance.dto.StaffResponseDTO;
import java.util.List;

public interface StaffService {
    StaffResponseDTO createStaff(StaffRequestDTO dto);
    List<StaffResponseDTO> getAllStaff();
    StaffResponseDTO getStaffById(Integer id);
    List<StaffResponseDTO> getStaffBySpecialization(String specialization);
    StaffResponseDTO updateStaff(Integer id, StaffRequestDTO dto);
    void deleteStaff(Integer id);
}