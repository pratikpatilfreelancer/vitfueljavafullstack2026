package com.ams.controller.api;

import com.ams.dto.StudentAttendanceStats;
import com.ams.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportApiController {

    private final ReportService reportService;

    public ReportApiController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public ResponseEntity<List<StudentAttendanceStats>> getReport(
            @RequestParam(required = false) Long batchId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "batch") String reportType) {

        LocalDate start = (startDate != null && !startDate.isBlank())
                ? LocalDate.parse(startDate) : LocalDate.now().minusDays(30);
        LocalDate end = (endDate != null && !endDate.isBlank())
                ? LocalDate.parse(endDate) : LocalDate.now();

        List<StudentAttendanceStats> stats;
        if ("percentage".equals(reportType)) {
            stats = reportService.getPercentageReport(batchId, start, end);
        } else if ("monthly".equals(reportType)) {
            stats = reportService.getMonthlyReport(batchId, start.getYear(), start.getMonthValue());
        } else {
            stats = reportService.getStudentWiseReport(batchId, start, end);
        }

        return ResponseEntity.ok(stats);
    }
}
