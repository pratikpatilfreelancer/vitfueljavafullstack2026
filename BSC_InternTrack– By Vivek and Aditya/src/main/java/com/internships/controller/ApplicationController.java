package com.internships.controller;

import com.internships.model.InternshipApplication;
import com.internships.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
@Tag(name = "Applications", description = "Application management APIs")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    @Operation(summary = "Get all applications")
    public ResponseEntity<List<InternshipApplication>> getAllApplications() {
        return ResponseEntity.ok(applicationService.getAllApplications());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get application by ID")
    public ResponseEntity<InternshipApplication> getApplicationById(@PathVariable String id) {
        return ResponseEntity.ok(applicationService.getApplicationById(id));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get applications by student ID")
    public ResponseEntity<List<InternshipApplication>> getApplicationsByStudentId(@PathVariable String studentId) {
        return ResponseEntity.ok(applicationService.getApplicationsByStudentId(studentId));
    }

    @GetMapping("/internship/{internshipId}")
    @Operation(summary = "Get applications by internship ID")
    public ResponseEntity<List<InternshipApplication>> getApplicationsByInternshipId(@PathVariable String internshipId) {
        return ResponseEntity.ok(applicationService.getApplicationsByInternshipId(internshipId));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get applications by status")
    public ResponseEntity<List<InternshipApplication>> getApplicationsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(applicationService.getApplicationsByStatus(status));
    }

    @GetMapping("/stats/all")
    @Operation(summary = "Get total application count")
    public ResponseEntity<Map<String, Long>> getTotalApplications() {
        return ResponseEntity.ok(Map.of("total", applicationService.countAll()));
    }

    @GetMapping("/stats/status/{status}")
    @Operation(summary = "Get application count by status")
    public ResponseEntity<Map<String, Long>> getApplicationsCountByStatus(@PathVariable String status) {
        return ResponseEntity.ok(Map.of("count", applicationService.countByStatus(status)));
    }

    @PostMapping
    @Operation(summary = "Create a new application")
    public ResponseEntity<InternshipApplication> createApplication(@Valid @RequestBody InternshipApplication application) {
        InternshipApplication created = applicationService.createApplication(application);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing application")
    public ResponseEntity<InternshipApplication> updateApplication(@PathVariable String id, @Valid @RequestBody InternshipApplication application) {
        return ResponseEntity.ok(applicationService.updateApplication(id, application));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update application status")
    public ResponseEntity<InternshipApplication> updateApplicationStatus(@PathVariable String id, @RequestParam String status) {
        return ResponseEntity.ok(applicationService.updateApplicationStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an application")
    public ResponseEntity<Void> deleteApplication(@PathVariable String id) {
        applicationService.deleteApplication(id);
        return ResponseEntity.noContent().build();
    }
}
