package com.ams.controller;

import com.ams.dto.ReportFilterDto;
import com.ams.dto.StudentAttendanceStats;
import com.ams.service.BatchService;
import com.ams.service.ReportService;
import com.ams.service.StudentService;
import com.ams.util.ExcelReportUtil;
import com.ams.util.PdfReportUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/reports")
public class AdminReportController {

    private final ReportService reportService;
    private final BatchService batchService;
    private final StudentService studentService;

    public AdminReportController(ReportService reportService,
                                  BatchService batchService,
                                  StudentService studentService) {
        this.reportService = reportService;
        this.batchService = batchService;
        this.studentService = studentService;
    }

    @GetMapping
    public String showReportPage(Model model) {
        ReportFilterDto filter = new ReportFilterDto();
        filter.setStartDate(LocalDate.now().minusDays(30));
        filter.setEndDate(LocalDate.now());
        filter.setReportType("batch");

        List<StudentAttendanceStats> stats = getStats(filter);
        model.addAttribute("filter", filter);
        model.addAttribute("batches", batchService.findAll());
        model.addAttribute("stats", stats);
        model.addAttribute("reportGenerated", true);
        return "admin/reports";
    }

    @PostMapping
    public String generateReport(@ModelAttribute("filter") ReportFilterDto filter,
                                  Model model) {
        List<StudentAttendanceStats> stats = getStats(filter);
        model.addAttribute("filter", filter);
        model.addAttribute("batches", batchService.findAll());
        model.addAttribute("stats", stats);
        model.addAttribute("reportGenerated", true);
        return "admin/reports";
    }

    @GetMapping("/export/excel")
    public void exportExcel(@RequestParam(required = false) Long batchId,
                             @RequestParam(required = false) String startDate,
                             @RequestParam(required = false) String endDate,
                             @RequestParam(required = false) String reportType,
                             HttpServletResponse response) throws Exception {
        ReportFilterDto filter = buildFilter(batchId, startDate, endDate, reportType);
        List<StudentAttendanceStats> stats = getStats(filter);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=attendance_report.xlsx");
        ExcelReportUtil.generateAttendanceReport(stats, "Attendance Report", response.getOutputStream());
    }

    @GetMapping("/export/pdf")
    public void exportPdf(@RequestParam(required = false) Long batchId,
                            @RequestParam(required = false) String startDate,
                            @RequestParam(required = false) String endDate,
                            @RequestParam(required = false) String reportType,
                            HttpServletResponse response) throws Exception {
        ReportFilterDto filter = buildFilter(batchId, startDate, endDate, reportType);
        List<StudentAttendanceStats> stats = getStats(filter);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=attendance_report.pdf");
        PdfReportUtil.generateAttendanceReport(stats, "Attendance Report", response.getOutputStream());
    }

    private List<StudentAttendanceStats> getStats(ReportFilterDto filter) {
        LocalDate start = filter.getStartDate() != null ? filter.getStartDate()
                : LocalDate.now().minusDays(30);
        LocalDate end = filter.getEndDate() != null ? filter.getEndDate() : LocalDate.now();

        if ("percentage".equals(filter.getReportType())) {
            return reportService.getPercentageReport(filter.getBatchId(), start, end);
        } else if ("monthly".equals(filter.getReportType())) {
            return reportService.getMonthlyReport(filter.getBatchId(),
                    start.getYear(), start.getMonthValue());
        } else {
            return reportService.getStudentWiseReport(filter.getBatchId(), start, end);
        }
    }

    private ReportFilterDto buildFilter(Long batchId, String startDate,
                                         String endDate, String reportType) {
        ReportFilterDto filter = new ReportFilterDto();
        filter.setBatchId(batchId);
        filter.setStartDate(startDate != null && !startDate.isBlank()
                ? LocalDate.parse(startDate) : null);
        filter.setEndDate(endDate != null && !endDate.isBlank()
                ? LocalDate.parse(endDate) : null);
        filter.setReportType(reportType);
        return filter;
    }
}
