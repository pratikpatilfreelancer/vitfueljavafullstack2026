package com.examapp.examapp.controller;

import com.examapp.examapp.service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Controller
@RequestMapping("/exams/{examId}/questions")
public class QuestionPageController {

    private final QuestionService questionService;

    public QuestionPageController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public String showAddQuestionForm(@PathVariable Long examId, Model model) {
        model.addAttribute("examId", examId);
        model.addAttribute("questions", questionService.getQuestionsByExamId(examId));
        return "add-question";
    }

    @PostMapping("/mcq")
    public String addMCQ(@PathVariable Long examId,
                          @RequestParam String questionText,
                          @RequestParam String option1,
                          @RequestParam String option2,
                          @RequestParam String option3,
                          @RequestParam int correctIndex,
                          Model model) {
        questionService.addMCQQuestion(examId, questionText, Arrays.asList(option1, option2, option3), correctIndex);
        model.addAttribute("examId", examId);
        return "add-question";
    }

    @PostMapping("/truefalse")
    public String addTrueFalse(@PathVariable Long examId,
                                @RequestParam String questionText,
                                @RequestParam boolean correctBoolean,
                                Model model) {
        questionService.addTrueFalseQuestion(examId, questionText, correctBoolean);
        model.addAttribute("examId", examId);
        return "add-question";
    }
}
