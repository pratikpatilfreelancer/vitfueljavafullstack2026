package com.ams.dto;

import java.util.List;

/**
 * DTO for submitting batch attendance (all students at once).
 */
public class AttendanceFormDto {

    private Long batchId;
    private String attendanceDate; // yyyy-MM-dd
    private List<Long> presentStudentIds;

    // Getters and Setters
    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }

    public String getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(String attendanceDate) { this.attendanceDate = attendanceDate; }

    public List<Long> getPresentStudentIds() { return presentStudentIds; }
    public void setPresentStudentIds(List<Long> presentStudentIds) { this.presentStudentIds = presentStudentIds; }
}
