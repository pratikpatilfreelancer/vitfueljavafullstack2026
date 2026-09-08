package com.capgemini.apartment_maintenance.dto;

import jakarta.validation.constraints.*;

public record ResidentRequestDTO(
        @NotBlank(message = "Name is required") String name,
        @NotBlank @Email(message = "Enter a valid email") String email,
        @NotBlank @Pattern(regexp = "^[0-9]{10,15}$", message = "Enter a valid phone number") String phone,
        @NotBlank(message = "Flat number is required") String flatNo,
        @NotNull(message = "Building ID is required") Integer buildingId
) {}