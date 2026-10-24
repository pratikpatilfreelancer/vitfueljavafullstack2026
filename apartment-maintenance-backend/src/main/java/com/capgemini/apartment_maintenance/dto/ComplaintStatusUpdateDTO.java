package com.capgemini.apartment_maintenance.dto;

import com.capgemini.apartment_maintenance.entity.ComplaintStatus;
import jakarta.validation.constraints.NotNull;

// used only for status-change endpoint, kept separate from full update
public record ComplaintStatusUpdateDTO(
        @NotNull(message = "Status is required") ComplaintStatus status
) {}