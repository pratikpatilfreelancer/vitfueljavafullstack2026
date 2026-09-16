package com.ams.controller.api;

import com.ams.dto.BatchDto;
import com.ams.dto.StudentDto;
import com.ams.dto.TeacherDto;
import com.ams.entity.Batch;
import com.ams.entity.Student;
import com.ams.entity.User;
import com.ams.service.BatchService;
import com.ams.service.StudentService;
import com.ams.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    private final UserService userService;
    private final StudentService studentService;
    private final BatchService batchService;

    public AdminApiController(UserService userService, StudentService studentService, BatchService batchService) {
        this.userService = userService;
        this.studentService = studentService;
        this.batchService = batchService;
    }

    // --- Teachers ---
    @GetMapping("/teachers")
    public ResponseEntity<List<User>> getAllTeachers() {
        return ResponseEntity.ok(userService.findAllTeachers());
    }

    @PostMapping("/teachers")
    public ResponseEntity<?> createTeacher(@Valid @RequestBody TeacherDto dto) {
        try {
            User created = userService.save(dto);
            return ResponseEntity.ok(Map.of("message", "Teacher created successfully", "teacherId", created.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // --- Students ---
    @GetMapping("/students")
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.findAll());
    }

    @PostMapping("/students")
    public ResponseEntity<?> createStudent(@Valid @RequestBody StudentDto dto) {
        try {
            Student created = studentService.save(dto);
            return ResponseEntity.ok(Map.of("message", "Student created successfully", "studentId", created.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // --- Batches ---
    @GetMapping("/batches")
    public ResponseEntity<List<Batch>> getAllBatches() {
        return ResponseEntity.ok(batchService.findAll());
    }

    @PostMapping("/batches")
    public ResponseEntity<?> createBatch(@Valid @RequestBody BatchDto dto) {
        try {
            Batch created = batchService.save(dto);
            return ResponseEntity.ok(Map.of("message", "Batch created successfully", "batchId", created.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
