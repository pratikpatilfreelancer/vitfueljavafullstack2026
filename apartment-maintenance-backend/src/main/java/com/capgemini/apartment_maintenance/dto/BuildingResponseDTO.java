package com.capgemini.apartment_maintenance.dto;

public record BuildingResponseDTO(
        Integer buildingId,
        String name,
        String address,
        Integer totalFlats,
        int residentCount   // derived field — handy for dashboard UI
) {}