package com.ams.controller;

import com.ams.service.BatchService;
import com.ams.service.StudentService;
import com.ams.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final UserService userService;
    private final StudentService studentService;
    private final BatchService batchService;

    public AdminDashboardController(UserService userService,
                                     StudentService studentService,
                                     BatchService batchService) {
        this.userService = userService;
        this.studentService = studentService;
        this.batchService = batchService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("teacherCount", userService.countTeachers());
        model.addAttribute("studentCount", studentService.count());
        model.addAttribute("batchCount", batchService.count());
        return "admin/dashboard";
    }
}
