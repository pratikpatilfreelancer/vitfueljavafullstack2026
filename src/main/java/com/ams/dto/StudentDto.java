package com.ams.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class StudentDto {

    private Long id;

    @NotBlank(message = "Enrollment number is required")
    @Size(max = 30, message = "Enrollment number must be at most 30 characters")
    private String enrollmentNo;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @NotNull(message = "Batch is required")
    private Long batchId;

    @Email(message = "Invalid email format")
    @Size(max = 100)
    private String email;

    @Size(max = 15, message = "Phone must be at most 15 characters")
    private String phone;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEnrollmentNo() { return enrollmentNo; }
    public void setEnrollmentNo(String enrollmentNo) { this.enrollmentNo = enrollmentNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
