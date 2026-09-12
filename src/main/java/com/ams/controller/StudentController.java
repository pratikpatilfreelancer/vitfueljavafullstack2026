package com.ams.controller;

import com.ams.dto.StudentDto;
import com.ams.entity.Student;
import com.ams.service.BatchService;
import com.ams.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/students")
public class StudentController {

    private final StudentService studentService;
    private final BatchService batchService;

    public StudentController(StudentService studentService, BatchService batchService) {
        this.studentService = studentService;
        this.batchService = batchService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("students", studentService.findAll());
        return "admin/students/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("studentDto", new StudentDto());
        model.addAttribute("batches", batchService.findAll());
        return "admin/students/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("studentDto") StudentDto dto,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("batches", batchService.findAll());
            return "admin/students/form";
        }
        try {
            studentService.save(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Student created successfully!");
        } catch (Exception e) {
            result.rejectValue("enrollmentNo", "duplicate", e.getMessage());
            model.addAttribute("batches", batchService.findAll());
            return "admin/students/form";
        }
        return "redirect:/admin/students";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Student student = studentService.findById(id);
        StudentDto dto = new StudentDto();
        dto.setId(student.getId());
        dto.setEnrollmentNo(student.getEnrollmentNo());
        dto.setName(student.getName());
        dto.setBatchId(student.getBatch() != null ? student.getBatch().getId() : null);
        dto.setEmail(student.getEmail());
        dto.setPhone(student.getPhone());
        model.addAttribute("studentDto", dto);
        model.addAttribute("batches", batchService.findAll());
        return "admin/students/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                          @Valid @ModelAttribute("studentDto") StudentDto dto,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            dto.setId(id);
            model.addAttribute("batches", batchService.findAll());
            return "admin/students/form";
        }
        try {
            studentService.update(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Student updated successfully!");
        } catch (Exception e) {
            result.rejectValue("enrollmentNo", "duplicate", e.getMessage());
            dto.setId(id);
            model.addAttribute("batches", batchService.findAll());
            return "admin/students/form";
        }
        return "redirect:/admin/students";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        studentService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Student deleted successfully!");
        return "redirect:/admin/students";
    }
}
