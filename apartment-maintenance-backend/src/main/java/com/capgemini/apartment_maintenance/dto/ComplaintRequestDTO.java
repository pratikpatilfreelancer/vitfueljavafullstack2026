package com.capgemini.apartment_maintenance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// used only for CREATE — staff is assigned later via a separate endpoint
public record ComplaintRequestDTO(
        @NotBlank(message = "Description is required") String description,
        @NotNull(message = "Resident ID is required") Integer residentId,
        @NotNull(message = "Category ID is required") Integer categoryId
) {}