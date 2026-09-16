package com.ams.controller;

import com.ams.dto.ReportFilterDto;
import com.ams.dto.StudentAttendanceStats;
import com.ams.entity.Batch;
import com.ams.entity.User;
import com.ams.repository.UserRepository;
import com.ams.service.BatchService;
import com.ams.service.ReportService;
import com.ams.util.ExcelReportUtil;
import com.ams.util.PdfReportUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/teacher/reports")
public class TeacherReportController {

    private final ReportService reportService;
    private final BatchService batchService;
    private final UserRepository userRepository;

    public TeacherReportController(ReportService reportService,
                                    BatchService batchService,
                                    UserRepository userRepository) {
        this.reportService = reportService;
        this.batchService = batchService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String showReportPage(Model model, Authentication authentication) {
        User teacher = getCurrentUser(authentication);
        List<Batch> batches = batchService.findByTeacherId(teacher.getId());

        ReportFilterDto filter = new ReportFilterDto();
        filter.setStartDate(LocalDate.now().minusDays(30));
        filter.setEndDate(LocalDate.now());
        filter.setReportType("batch");

        List<StudentAttendanceStats> stats = getStats(filter);
        model.addAttribute("filter", filter);
        model.addAttribute("batches", batches);
        model.addAttribute("stats", stats);
        model.addAttribute("reportGenerated", true);
        return "teacher/reports";
    }

    @PostMapping
    public String generateReport(@ModelAttribute("filter") ReportFilterDto filter,
                                  Model model,
                                  Authentication authentication) {
        User teacher = getCurrentUser(authentication);
        verifyBatchOwnership(filter.getBatchId(), teacher);

        List<StudentAttendanceStats> stats = getStats(filter);
        List<Batch> batches = batchService.findByTeacherId(teacher.getId());

        model.addAttribute("filter", filter);
        model.addAttribute("batches", batches);
        model.addAttribute("stats", stats);
        model.addAttribute("reportGenerated", true);
        return "teacher/reports";
    }

    @GetMapping("/export/excel")
    public void exportExcel(@RequestParam(required = false) Long batchId,
                             @RequestParam(required = false) String startDate,
                             @RequestParam(required = false) String endDate,
                             @RequestParam(required = false) String reportType,
                             Authentication authentication,
                             HttpServletResponse response) throws Exception {
        User teacher = getCurrentUser(authentication);
        verifyBatchOwnership(batchId, teacher);

        ReportFilterDto filter = buildFilter(batchId, startDate, endDate, reportType);
        List<StudentAttendanceStats> stats = getStats(filter);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=attendance_report.xlsx");
        ExcelReportUtil.generateAttendanceReport(stats, "Attendance Report", response.getOutputStream());
    }

    @GetMapping("/export/pdf")
    public void exportPdf(@RequestParam(required = false) Long batchId,
                            @RequestParam(required = false) String startDate,
                            @RequestParam(required = false) String endDate,
                            @RequestParam(required = false) String reportType,
                            Authentication authentication,
                            HttpServletResponse response) throws Exception {
        User teacher = getCurrentUser(authentication);
        verifyBatchOwnership(batchId, teacher);

        ReportFilterDto filter = buildFilter(batchId, startDate, endDate, reportType);
        List<StudentAttendanceStats> stats = getStats(filter);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=attendance_report.pdf");
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

    private void verifyBatchOwnership(Long batchId, User teacher) {
        if (batchId != null) {
            Batch batch = batchService.findById(batchId);
            if (!batch.getTeacher().getId().equals(teacher.getId())) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "You are not assigned to this batch.");
            }
        }
    }

    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
