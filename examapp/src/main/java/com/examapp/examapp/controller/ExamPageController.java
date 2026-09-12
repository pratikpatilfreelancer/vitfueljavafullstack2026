package com.examapp.examapp.controller;

import com.examapp.examapp.entity.Exam;
import com.examapp.examapp.service.ExamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ExamPageController {

    private final ExamService examService;

    public ExamPageController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping("/exams/create")
    public String showCreateForm() {
        return "create-exam";
    }

    @PostMapping("/exams/create")
    public String createExam(@RequestParam String title,
                              @RequestParam int totalMarks,
                              @RequestParam String createdBy,
                              Model model) {
        Exam exam = new Exam();
        exam.setTitle(title);
        exam.setTotalMarks(totalMarks);
        exam.setCreatedBy(createdBy);
        Exam saved = examService.createExam(exam);

        model.addAttribute("exam", saved);
        return "exam-created";
    }

    @GetMapping("/exams")
    public String listExams(Model model) {
        model.addAttribute("exams", examService.getAllExams());
        return "exam-list";
    }

    @GetMapping("/exams/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("exam", examService.getExamById(id));
        return "exam-edit";
    }

    @PostMapping("/exams/{id}/edit")
    public String updateExam(@PathVariable Long id, @RequestParam String title, @RequestParam int totalMarks) {
        Exam exam = new Exam();
        exam.setTitle(title);
        exam.setTotalMarks(totalMarks);
        examService.updateExam(id, exam);
        return "redirect:/exams";
    }

    @PostMapping("/exams/{id}/delete")
    public String deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return "redirect:/exams";
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }
}