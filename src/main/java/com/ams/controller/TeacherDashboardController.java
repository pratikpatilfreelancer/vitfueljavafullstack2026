package com.ams.controller;

import com.ams.entity.Batch;
import com.ams.entity.User;
import com.ams.repository.UserRepository;
import com.ams.service.BatchService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherDashboardController {

    private final BatchService batchService;
    private final UserRepository userRepository;

    public TeacherDashboardController(BatchService batchService, UserRepository userRepository) {
        this.batchService = batchService;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        User teacher = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Batch> batches = batchService.findByTeacherId(teacher.getId());
        model.addAttribute("teacher", teacher);
        model.addAttribute("batches", batches);
        return "teacher/dashboard";
    }
}
