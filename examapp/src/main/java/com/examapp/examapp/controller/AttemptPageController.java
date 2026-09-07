package com.examapp.examapp.controller;

import com.examapp.examapp.entity.Attempt;
import com.examapp.examapp.entity.Exam;
import com.examapp.examapp.service.AttemptService;
import com.examapp.examapp.service.ExamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/exams/{examId}/attempt")
public class AttemptPageController {

    private final AttemptService attemptService;
    private final ExamService examService;

    public AttemptPageController(AttemptService attemptService, ExamService examService) {
        this.attemptService = attemptService;
        this.examService = examService;
    }

    @GetMapping
    public String showAttemptForm(@PathVariable Long examId, Model model) {
        Exam exam = examService.getExamById(examId);
        model.addAttribute("exam", exam);
        return "attempt-exam";
    }

    @PostMapping
    public String submitAttempt(@PathVariable Long examId,
                                 @RequestParam String candidateName,
                                 @RequestParam Map<String, String> allParams,
                                 Model model) {

        Map<Long, String> answers = new HashMap<>();
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (entry.getKey().startsWith("answer_")) {
                Long questionId = Long.parseLong(entry.getKey().replace("answer_", ""));
                answers.put(questionId, entry.getValue());
            }
        }

        Attempt attempt = attemptService.submitAttempt(examId, candidateName, answers);
        model.addAttribute("attempt", attempt);
        return "result";
    }
}