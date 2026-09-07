package com.examapp.examapp.controller;

import com.examapp.examapp.entity.Attempt;
import com.examapp.examapp.service.AttemptService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/exams")
public class AttemptController {

    private final AttemptService attemptService;

    public AttemptController(AttemptService attemptService) {
        this.attemptService = attemptService;
    }

    @PostMapping("/{examId}/attempt")
    public Attempt submitAttempt(
            @PathVariable Long examId,
            @RequestParam String candidateName,
            @RequestBody Map<Long, String> answers) {
        return attemptService.submitAttempt(examId, candidateName, answers);
    }
}