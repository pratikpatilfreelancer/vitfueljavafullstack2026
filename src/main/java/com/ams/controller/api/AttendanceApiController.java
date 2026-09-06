package com.ams.controller.api;

import com.ams.dto.AttendanceFormDto;
import com.ams.entity.Attendance;
import com.ams.entity.User;
import com.ams.repository.UserRepository;
import com.ams.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher")
public class AttendanceApiController {

    private final AttendanceService attendanceService;
    private final UserRepository userRepository;

    public AttendanceApiController(AttendanceService attendanceService, UserRepository userRepository) {
        this.attendanceService = attendanceService;
        this.userRepository = userRepository;
    }

    @PostMapping("/attendance")
    public ResponseEntity<?> markAttendance(@RequestBody AttendanceFormDto form,
                                             @RequestParam(defaultValue = "john.smith") String username) {
        try {
            User teacher = userRepository.findByUsername(username).orElseThrow();
            LocalDate date = (form.getAttendanceDate() != null && !form.getAttendanceDate().isBlank())
                    ? LocalDate.parse(form.getAttendanceDate()) : LocalDate.now();
            List<Long> presentIds = form.getPresentStudentIds() != null ? form.getPresentStudentIds() : List.of();

            attendanceService.markBatchAttendance(form.getBatchId(), date, presentIds, teacher);
            return ResponseEntity.ok(Map.of("message", "Attendance marked successfully", "date", date.toString()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/absentees")
    public ResponseEntity<?> markAbsentees(@RequestBody AttendanceFormDto form,
                                           @RequestParam(defaultValue = "john.smith") String username) {
        try {
            User teacher = userRepository.findByUsername(username).orElseThrow();
            LocalDate date = (form.getAttendanceDate() != null && !form.getAttendanceDate().isBlank())
                    ? LocalDate.parse(form.getAttendanceDate()) : LocalDate.now();
            List<Long> absentIds = form.getPresentStudentIds() != null ? form.getPresentStudentIds() : List.of();

            attendanceService.markAbsenteesOnly(form.getBatchId(), date, absentIds, teacher);
            return ResponseEntity.ok(Map.of("message", "Absentees marked successfully", "date", date.toString()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/attendance/{batchId}")
    public ResponseEntity<?> getAttendance(@PathVariable Long batchId,
                                            @RequestParam(required = false) String date) {
        LocalDate searchDate = (date != null && !date.isBlank()) ? LocalDate.parse(date) : LocalDate.now();
        List<Attendance> records = attendanceService.getAttendanceByBatchAndDate(batchId, searchDate);
        return ResponseEntity.ok(records);
    }
}
