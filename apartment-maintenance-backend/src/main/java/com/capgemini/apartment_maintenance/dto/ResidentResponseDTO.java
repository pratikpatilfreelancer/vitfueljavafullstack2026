package com.capgemini.apartment_maintenance.dto;

public record ResidentResponseDTO(
        Integer residentId,
        String name,
        String email,
        String phone,
        String flatNo,
        String buildingName   // flat — resolved from Resident.building.name
) {}