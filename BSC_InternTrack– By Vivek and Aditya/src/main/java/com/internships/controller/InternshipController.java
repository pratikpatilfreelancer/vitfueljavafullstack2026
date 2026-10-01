package com.internships.controller;

import com.internships.model.Internship;
import com.internships.service.InternshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/internships")
@Tag(name = "Internships", description = "Internship management APIs")
public class InternshipController {

    private final InternshipService internshipService;

    public InternshipController(InternshipService internshipService) {
        this.internshipService = internshipService;
    }

    @GetMapping
    @Operation(summary = "Get all internships")
    public ResponseEntity<List<Internship>> getAllInternships() {
        return ResponseEntity.ok(internshipService.getAllInternships());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get internship by ID")
    public ResponseEntity<Internship> getInternshipById(@PathVariable String id) {
        return ResponseEntity.ok(internshipService.getInternshipById(id));
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "Get internships by company ID")
    public ResponseEntity<List<Internship>> getInternshipsByCompanyId(@PathVariable String companyId) {
        return ResponseEntity.ok(internshipService.getInternshipsByCompanyId(companyId));
    }

    @PostMapping
    @Operation(summary = "Create a new internship")
    public ResponseEntity<Internship> createInternship(@Valid @RequestBody Internship internship) {
        Internship created = internshipService.createInternship(internship);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing internship")
    public ResponseEntity<Internship> updateInternship(@PathVariable String id, @Valid @RequestBody Internship internship) {
        return ResponseEntity.ok(internshipService.updateInternship(id, internship));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an internship")
    public ResponseEntity<Void> deleteInternship(@PathVariable String id) {
        internshipService.deleteInternship(id);
        return ResponseEntity.noContent().build();
    }
}
