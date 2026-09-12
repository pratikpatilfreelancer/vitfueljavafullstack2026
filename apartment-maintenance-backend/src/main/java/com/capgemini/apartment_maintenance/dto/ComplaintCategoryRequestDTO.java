package com.capgemini.apartment_maintenance.dto;

import jakarta.validation.constraints.NotBlank;

public record ComplaintCategoryRequestDTO(
        @NotBlank(message = "Category name is required") String categoryName
) {}