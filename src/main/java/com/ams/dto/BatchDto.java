package com.ams.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BatchDto {

    private Long id;

    @NotBlank(message = "Batch code is required")
    @Size(max = 20, message = "Batch code must be at most 20 characters")
    private String batchCode;

    @NotBlank(message = "Subject is required")
    @Size(max = 100, message = "Subject must be at most 100 characters")
    private String subject;

    @NotNull(message = "Teacher is required")
    private Long teacherId;

    @Size(max = 20, message = "Batch days must be at most 20 characters")
    private String batchDays;

    @Size(max = 20, message = "Batch timing must be at most 20 characters")
    private String batchTiming;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBatchCode() { return batchCode; }
    public void setBatchCode(String batchCode) { this.batchCode = batchCode; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public String getBatchDays() { return batchDays; }
    public void setBatchDays(String batchDays) { this.batchDays = batchDays; }

    public String getBatchTiming() { return batchTiming; }
    public void setBatchTiming(String batchTiming) { this.batchTiming = batchTiming; }
}
