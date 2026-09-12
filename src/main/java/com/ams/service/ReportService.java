package com.ams.service;

import com.ams.dto.StudentAttendanceStats;
import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    List<StudentAttendanceStats> getStudentWiseReport(Long batchId, LocalDate start, LocalDate end);
    List<StudentAttendanceStats> getPercentageReport(Long batchId, LocalDate start, LocalDate end);
    List<StudentAttendanceStats> getMonthlyReport(Long batchId, int year, int month);
}
