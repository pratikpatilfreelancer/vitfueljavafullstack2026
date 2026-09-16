package com.ams.controller;

import com.ams.dto.BatchDto;
import com.ams.entity.Batch;
import com.ams.service.BatchService;
import com.ams.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/batches")
public class BatchController {

    private final BatchService batchService;
    private final UserService userService;

    public BatchController(BatchService batchService, UserService userService) {
        this.batchService = batchService;
        this.userService = userService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("batches", batchService.findAll());
        return "admin/batches/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("batchDto", new BatchDto());
        model.addAttribute("teachers", userService.findAllTeachers());
        return "admin/batches/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("batchDto") BatchDto dto,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("teachers", userService.findAllTeachers());
            return "admin/batches/form";
        }
        try {
            batchService.save(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Batch created successfully!");
        } catch (Exception e) {
            result.rejectValue("batchCode", "duplicate", e.getMessage());
            model.addAttribute("teachers", userService.findAllTeachers());
            return "admin/batches/form";
        }
        return "redirect:/admin/batches";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Batch batch = batchService.findById(id);
        BatchDto dto = new BatchDto();
        dto.setId(batch.getId());
        dto.setBatchCode(batch.getBatchCode());
        dto.setSubject(batch.getSubject());
        dto.setTeacherId(batch.getTeacher() != null ? batch.getTeacher().getId() : null);
        dto.setBatchDays(batch.getBatchDays());
        dto.setBatchTiming(batch.getBatchTiming());
        model.addAttribute("batchDto", dto);
        model.addAttribute("teachers", userService.findAllTeachers());
        return "admin/batches/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                          @Valid @ModelAttribute("batchDto") BatchDto dto,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            dto.setId(id);
            model.addAttribute("teachers", userService.findAllTeachers());
            return "admin/batches/form";
        }
        try {
            batchService.update(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Batch updated successfully!");
        } catch (Exception e) {
            result.rejectValue("batchCode", "duplicate", e.getMessage());
            dto.setId(id);
            model.addAttribute("teachers", userService.findAllTeachers());
            return "admin/batches/form";
        }
        return "redirect:/admin/batches";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        batchService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Batch deleted successfully!");
        return "redirect:/admin/batches";
    }
}
