package com.capgemini.apartment_maintenance.dto;

public record StaffResponseDTO(
        Integer staffId,
        String name,
        String designation,
        String specialization
) {}