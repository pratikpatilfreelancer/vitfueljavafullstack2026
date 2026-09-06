package com.ams.controller;

import com.ams.entity.User;
import com.ams.repository.UserRepository;
import com.ams.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/teacher/settings")
public class TeacherSettingsController {

    private final UserService userService;
    private final UserRepository userRepository;

    public TeacherSettingsController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String settings(Model model, Authentication authentication) {
        User teacher = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("teacher", teacher);
        return "teacher/settings";
    }

    @PostMapping
    public String changePassword(@RequestParam String currentPassword,
                                  @RequestParam String newPassword,
                                  @RequestParam String confirmPassword,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match.");
            return "redirect:/teacher/settings";
        }
        if (newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "New password must be at least 6 characters.");
            return "redirect:/teacher/settings";
        }

        User teacher = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            userService.changePassword(teacher.getId(), newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/teacher/settings";
    }
}
