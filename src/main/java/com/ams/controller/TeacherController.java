package com.ams.controller;

import com.ams.dto.TeacherDto;
import com.ams.entity.User;
import com.ams.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/teachers")
public class TeacherController {

    private final UserService userService;

    public TeacherController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("teachers", userService.findAllTeachers());
        return "admin/teachers/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("teacherDto", new TeacherDto());
        return "admin/teachers/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("teacherDto") TeacherDto dto,
                          BindingResult result,
                          RedirectAttributes redirectAttributes) {
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            result.rejectValue("password", "NotBlank", "Password is required for new teacher");
        }
        if (result.hasErrors()) {
            return "admin/teachers/form";
        }
        try {
            userService.save(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Teacher created successfully!");
        } catch (Exception e) {
            result.rejectValue("username", "duplicate", e.getMessage());
            return "admin/teachers/form";
        }
        return "redirect:/admin/teachers";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        User teacher = userService.findById(id);
        TeacherDto dto = new TeacherDto();
        dto.setId(teacher.getId());
        dto.setFullName(teacher.getFullName());
        dto.setUsername(teacher.getUsername());
        dto.setEmail(teacher.getEmail());
        model.addAttribute("teacherDto", dto);
        return "admin/teachers/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                          @Valid @ModelAttribute("teacherDto") TeacherDto dto,
                          BindingResult result,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            dto.setId(id);
            return "admin/teachers/form";
        }
        try {
            userService.update(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Teacher updated successfully!");
        } catch (Exception e) {
            result.rejectValue("username", "duplicate", e.getMessage());
            dto.setId(id);
            return "admin/teachers/form";
        }
        return "redirect:/admin/teachers";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Teacher deleted successfully!");
        return "redirect:/admin/teachers";
    }
}
