package com.ams.controller;

import com.ams.dto.AttendanceFormDto;
import com.ams.entity.*;
import com.ams.exception.FutureDateException;
import com.ams.repository.UserRepository;
import com.ams.service.AttendanceService;
import com.ams.service.BatchService;
import com.ams.service.StudentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/teacher")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final BatchService batchService;
    private final StudentService studentService;
    private final UserRepository userRepository;

    public AttendanceController(AttendanceService attendanceService,
                                 BatchService batchService,
                                 StudentService studentService,
                                 UserRepository userRepository) {
        this.attendanceService = attendanceService;
        this.batchService = batchService;
        this.studentService = studentService;
        this.userRepository = userRepository;
    }

    @GetMapping("/batches/{id}/attendance")
    public String showAttendanceForm(@PathVariable Long id,
                                      @RequestParam(required = false) String date,
                                      Model model,
                                      Authentication authentication) {
        Batch batch = batchService.findById(id);
        verifyTeacherOwnership(batch, authentication);

        LocalDate attendanceDate = (date != null && !date.isBlank())
                ? LocalDate.parse(date) : LocalDate.now();

        List<Student> students = studentService.findByBatchId(id);
        boolean alreadyMarked = attendanceService.isAttendanceMarked(id, attendanceDate);
        List<Attendance> existingRecords = attendanceService.getAttendanceByBatchAndDate(id, attendanceDate);

        model.addAttribute("batch", batch);
        model.addAttribute("students", students);
        model.addAttribute("attendanceDate", attendanceDate);
        model.addAttribute("selectedDate", attendanceDate);
        model.addAttribute("alreadyMarked", alreadyMarked);
        model.addAttribute("attendanceRecords", existingRecords);
        model.addAttribute("existingRecords", existingRecords);
        model.addAttribute("attendanceForm", new AttendanceFormDto());
        model.addAttribute("today", LocalDate.now());
        return "teacher/attendance";
    }

    @PostMapping("/batches/{id}/attendance")
    public String markAttendance(@PathVariable Long id,
                                  @ModelAttribute AttendanceFormDto form,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        Batch batch = batchService.findById(id);
        verifyTeacherOwnership(batch, authentication);
        User teacher = getCurrentUser(authentication);

        try {
            LocalDate date = (form.getAttendanceDate() != null && !form.getAttendanceDate().isBlank())
                    ? LocalDate.parse(form.getAttendanceDate()) : LocalDate.now();
            List<Long> presentIds = form.getPresentStudentIds() != null
                    ? form.getPresentStudentIds() : List.of();
            attendanceService.markBatchAttendance(id, date, presentIds, teacher);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Attendance marked successfully for " + date);
        } catch (FutureDateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error marking attendance: " + e.getMessage());
        }
        return "redirect:/teacher/batches/" + id + "/attendance";
    }

    @GetMapping("/batches/{id}/attendance/absentees")
    public String showAbsenteesForm(@PathVariable Long id,
                                     @RequestParam(required = false) String date,
                                     Model model,
                                     Authentication authentication) {
        Batch batch = batchService.findById(id);
        verifyTeacherOwnership(batch, authentication);

        LocalDate attendanceDate = (date != null && !date.isBlank())
                ? LocalDate.parse(date) : LocalDate.now();

        List<Student> students = studentService.findByBatchId(id);
        boolean alreadyMarked = attendanceService.isAttendanceMarked(id, attendanceDate);
        List<Attendance> existingRecords = attendanceService.getAttendanceByBatchAndDate(id, attendanceDate);

        model.addAttribute("batch", batch);
        model.addAttribute("students", students);
        model.addAttribute("attendanceDate", attendanceDate);
        model.addAttribute("selectedDate", attendanceDate);
        model.addAttribute("alreadyMarked", alreadyMarked);
        model.addAttribute("attendanceRecords", existingRecords);
        model.addAttribute("existingRecords", existingRecords);
        model.addAttribute("attendanceForm", new AttendanceFormDto());
        model.addAttribute("today", LocalDate.now());
        return "teacher/absentees";
    }

    @PostMapping("/batches/{id}/attendance/absentees")
    public String markAbsentees(@PathVariable Long id,
                                 @ModelAttribute AttendanceFormDto form,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        Batch batch = batchService.findById(id);
        verifyTeacherOwnership(batch, authentication);
        User teacher = getCurrentUser(authentication);

        try {
            LocalDate date = (form.getAttendanceDate() != null && !form.getAttendanceDate().isBlank())
                    ? LocalDate.parse(form.getAttendanceDate()) : LocalDate.now();
            List<Long> absentIds = form.getPresentStudentIds() != null
                    ? form.getPresentStudentIds() : List.of();
            attendanceService.markAbsenteesOnly(id, date, absentIds, teacher);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Absentees marked successfully for " + date);
        } catch (FutureDateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error marking absentees: " + e.getMessage());
        }
        return "redirect:/teacher/batches/" + id + "/attendance/absentees";
    }

    private void verifyTeacherOwnership(Batch batch, Authentication authentication) {
        User teacher = getCurrentUser(authentication);
        if (!batch.getTeacher().getId().equals(teacher.getId())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You are not assigned to this batch.");
        }
    }

    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
