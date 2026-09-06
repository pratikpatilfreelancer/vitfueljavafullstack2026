package com.example.leavemanagement.controller;

import com.example.leavemanagement.service.EmployeeService;
import com.example.leavemanagement.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for the dashboard / home page.
 * Shows simple summary counts: total employees, total/pending/approved/
 * rejected leave requests.
 */
@Controller
public class DashboardController {

    private final EmployeeService employeeService;
    private final LeaveRequestService leaveRequestService;

    @Autowired
    public DashboardController(EmployeeService employeeService, LeaveRequestService leaveRequestService) {
        this.employeeService = employeeService;
        this.leaveRequestService = leaveRequestService;
    }

    /**
     * Maps both "/" and "/dashboard" to the same dashboard page.
     * Model.addAttribute() puts data into the request so the Thymeleaf
     * template (dashboard.html) can read it with th:text="${...}".
     */
    @GetMapping({"/", "/dashboard"})
    public String showDashboard(Model model) {
        model.addAttribute("totalEmployees", employeeService.countEmployees());
        model.addAttribute("totalLeaves", leaveRequestService.countAllLeaveRequests());
        model.addAttribute("pendingLeaves", leaveRequestService.countPendingLeaveRequests());
        model.addAttribute("approvedLeaves", leaveRequestService.countApprovedLeaveRequests());
        model.addAttribute("rejectedLeaves", leaveRequestService.countRejectedLeaveRequests());
        return "dashboard"; // resolves to templates/dashboard.html
    }
}
