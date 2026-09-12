package com.capgemini.apartment_maintenance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BuildingRequestDTO(
        @NotBlank(message = "Building name is required") String name,
        @NotBlank(message = "Address is required") String address,
        @NotNull @Positive(message = "Total flats must be positive") Integer totalFlats
) {}