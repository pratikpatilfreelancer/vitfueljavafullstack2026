package com.internships.controller;

import com.internships.model.InternshipSkill;
import com.internships.service.InternshipSkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/internship-skills")
@Tag(name = "Internship Skills", description = "Internship skills mapping APIs")
public class InternshipSkillController {

    private final InternshipSkillService internshipSkillService;

    public InternshipSkillController(InternshipSkillService internshipSkillService) {
        this.internshipSkillService = internshipSkillService;
    }

    @GetMapping("/internship/{internshipId}")
    @Operation(summary = "Get skills by internship ID")
    public ResponseEntity<List<InternshipSkill>> getSkillsByInternshipId(@PathVariable String internshipId) {
        return ResponseEntity.ok(internshipSkillService.getSkillsByInternshipId(internshipId));
    }

    @GetMapping("/skill/{skillId}")
    @Operation(summary = "Get internships by skill ID")
    public ResponseEntity<List<InternshipSkill>> getInternshipsBySkillId(@PathVariable String skillId) {
        return ResponseEntity.ok(internshipSkillService.getInternshipsBySkillId(skillId));
    }

    @PostMapping
    @Operation(summary = "Add skill to internship")
    public ResponseEntity<Void> addSkillToInternship(@RequestBody InternshipSkill internshipSkill) {
        internshipSkillService.addSkillToInternship(internshipSkill.getInternshipId(), internshipSkill.getSkillId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping
    @Operation(summary = "Remove skill from internship")
    public ResponseEntity<Void> removeSkillFromInternship(@RequestBody InternshipSkill internshipSkill) {
        internshipSkillService.removeSkillFromInternship(internshipSkill.getInternshipId(), internshipSkill.getSkillId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/internship/{internshipId}")
    @Operation(summary = "Remove all skills from internship")
    public ResponseEntity<Void> removeAllSkillsFromInternship(@PathVariable String internshipId) {
        internshipSkillService.removeAllSkillsFromInternship(internshipId);
        return ResponseEntity.noContent().build();
    }
}
