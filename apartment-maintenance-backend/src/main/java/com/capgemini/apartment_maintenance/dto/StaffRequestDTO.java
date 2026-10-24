package com.capgemini.apartment_maintenance.dto;

import jakarta.validation.constraints.NotBlank;

public record StaffRequestDTO(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Designation is required") String designation,
        @NotBlank(message = "Specialization is required") String specialization
) {}