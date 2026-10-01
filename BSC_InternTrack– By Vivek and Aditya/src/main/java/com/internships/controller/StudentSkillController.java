package com.internships.controller;

import com.internships.model.StudentSkill;
import com.internships.service.StudentSkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-skills")
@Tag(name = "Student Skills", description = "Student skills mapping APIs")
public class StudentSkillController {

    private final StudentSkillService studentSkillService;

    public StudentSkillController(StudentSkillService studentSkillService) {
        this.studentSkillService = studentSkillService;
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get skills by student ID")
    public ResponseEntity<List<StudentSkill>> getSkillsByStudentId(@PathVariable String studentId) {
        return ResponseEntity.ok(studentSkillService.getSkillsByStudentId(studentId));
    }

    @GetMapping("/skill/{skillId}")
    @Operation(summary = "Get students by skill ID")
    public ResponseEntity<List<StudentSkill>> getStudentsBySkillId(@PathVariable String skillId) {
        return ResponseEntity.ok(studentSkillService.getStudentsBySkillId(skillId));
    }

    @PostMapping
    @Operation(summary = "Add skill to student")
    public ResponseEntity<Void> addSkillToStudent(@RequestBody StudentSkill studentSkill) {
        studentSkillService.addSkillToStudent(studentSkill.getStudentId(), studentSkill.getSkillId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping
    @Operation(summary = "Remove skill from student")
    public ResponseEntity<Void> removeSkillFromStudent(@RequestBody StudentSkill studentSkill) {
        studentSkillService.removeSkillFromStudent(studentSkill.getStudentId(), studentSkill.getSkillId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/student/{studentId}")
    @Operation(summary = "Remove all skills from student")
    public ResponseEntity<Void> removeAllSkillsFromStudent(@PathVariable String studentId) {
        studentSkillService.removeAllSkillsFromStudent(studentId);
        return ResponseEntity.noContent().build();
    }
}
