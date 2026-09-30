package com.example.leavemanagement.controller;

import com.example.leavemanagement.entity.LeaveRequest;
import com.example.leavemanagement.service.EmployeeService;
import com.example.leavemanagement.service.LeaveRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controller (Web/Presentation Layer) for Leave Request operations:
 * apply for leave, view all requests, and approve/reject a request.
 */
@Controller
@RequestMapping("/leaves")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;
    private final EmployeeService employeeService;

    @Autowired
    public LeaveRequestController(LeaveRequestService leaveRequestService, EmployeeService employeeService) {
        this.leaveRequestService = leaveRequestService;
        this.employeeService = employeeService;
    }

    /** READ (list): GET /leaves - shows every leave request with status
     *  and approve/reject buttons. */
    @GetMapping
    public String listLeaveRequests(Model model) {
        model.addAttribute("leaveRequests", leaveRequestService.getAllLeaveRequests());
        return "leaves/list"; // templates/leaves/list.html
    }

    /** CREATE (form): GET /leaves/apply - shows the "apply for leave"
     *  form, with a dropdown of employees to choose from. */
    @GetMapping("/apply")
    public String showApplyForm(Model model) {
        model.addAttribute("leaveRequest", new LeaveRequest());
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "leaves/form"; // templates/leaves/form.html
    }

    /**
     * CREATE (submit): POST /leaves/save
     * Saves a new leave application with status = PENDING by default.
     *
     * IMPORTANT: the form only submits the selected employee's ID (see
     * leaves/form.html - th:field="*{employee.id}"). Spring MVC creates a
     * "placeholder" Employee object with just that ID set, but this object
     * is NOT known to Hibernate/JPA as an existing database row - it's
     * treated as a new/transient object. If we saved leaveRequest as-is,
     * Hibernate would throw a TransientPropertyValueException because it
     * doesn't know whether this Employee already exists.
     *
     * The fix: look up the REAL, full Employee from the database using
     * that ID, and attach that to the leave request before saving.
     */
    @PostMapping("/save")
    public String applyForLeave(@Valid @ModelAttribute("leaveRequest") LeaveRequest leaveRequest,
                                 BindingResult bindingResult,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            // Need to re-populate the employee dropdown if we re-show the form
            model.addAttribute("employees", employeeService.getAllEmployees());
            return "leaves/form";
        }

        // Re-attach the fully-loaded, managed Employee entity (see comment above)
        Long selectedEmployeeId = leaveRequest.getEmployee().getId();
        leaveRequest.setEmployee(employeeService.getEmployeeById(selectedEmployeeId));

        leaveRequestService.applyForLeave(leaveRequest);
        return "redirect:/leaves";
    }

    /** UPDATE: GET /leaves/approve/{id} - marks a leave request as
     *  APPROVED. Kept as a simple GET link for beginner-friendliness. */
    @GetMapping("/approve/{id}")
    public String approveLeaveRequest(@PathVariable Long id) {
        leaveRequestService.approveLeaveRequest(id);
        return "redirect:/leaves";
    }

    /** UPDATE: GET /leaves/reject/{id} - marks a leave request as
     *  REJECTED. */
    @GetMapping("/reject/{id}")
    public String rejectLeaveRequest(@PathVariable Long id) {
        leaveRequestService.rejectLeaveRequest(id);
        return "redirect:/leaves";
    }

    /** DELETE: GET /leaves/delete/{id} - removes a leave request. */
    @GetMapping("/delete/{id}")
    public String deleteLeaveRequest(@PathVariable Long id) {
        leaveRequestService.deleteLeaveRequest(id);
        return "redirect:/leaves";
    }
}
