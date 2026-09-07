package com.examapp.examapp.controller;

import com.examapp.examapp.entity.MCQQuestion;
import com.examapp.examapp.entity.TrueFalseQuestion;
import com.examapp.examapp.service.QuestionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exams")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/{examId}/questions/mcq")
    public MCQQuestion addMCQQuestion(@PathVariable Long examId, @RequestBody Map<String, Object> body) {
        String text = (String) body.get("questionText");
        List<String> options = (List<String>) body.get("options");
        int correctIndex = (int) body.get("correctIndex");
        return questionService.addMCQQuestion(examId, text, options, correctIndex);
    }

    @PostMapping("/{examId}/questions/truefalse")
    public TrueFalseQuestion addTrueFalseQuestion(@PathVariable Long examId, @RequestBody Map<String, Object> body) {
        String text = (String) body.get("questionText");
        boolean correctBoolean = (boolean) body.get("correctBoolean");
        return questionService.addTrueFalseQuestion(examId, text, correctBoolean);
    }
}