package com.capgemini.apartment_maintenance.dto;

import com.capgemini.apartment_maintenance.entity.ComplaintStatus;
import java.time.LocalDate;

public record ComplaintResponseDTO(
        Integer complaintId,
        String description,
        ComplaintStatus status,
        LocalDate createdDate,
        LocalDate resolvedDate,
        String residentName,   // flat
        String staffName,      // flat — null if unassigned
        String categoryName    // flat
) {}