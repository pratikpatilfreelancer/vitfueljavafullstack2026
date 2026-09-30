package com.example.leavemanagement.controller;

import com.example.leavemanagement.entity.Employee;
import com.example.leavemanagement.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controller (Web/Presentation Layer) for Employee CRUD operations.
 *
 * Each method maps an HTTP request (GET/POST) to a Java method, calls the
 * Service layer to do the actual work, and returns the name of a
 * Thymeleaf template to render (or a redirect).
 */
@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /** READ (list): GET /employees - shows every employee in a table. */
    @GetMapping
    public String listEmployees(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "employees/list"; // templates/employees/list.html
    }

    /** CREATE (form): GET /employees/new - shows a blank add-employee form. */
    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "employees/form"; // templates/employees/form.html
    }

    /** UPDATE (form): GET /employees/edit/{id} - pre-fills the form with
     *  the existing employee's data. */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("employee", employeeService.getEmployeeById(id));
        return "employees/form";
    }

    /**
     * CREATE/UPDATE (submit): POST /employees/save
     * Handles both "add new" and "edit existing" because Employee's id
     * field decides whether JPA does an INSERT or an UPDATE.
     *
     * @Valid triggers the validation annotations on Employee (e.g.
     * @NotBlank, @Email). If validation fails, BindingResult captures
     * the errors and we re-show the form instead of saving bad data.
     */
    @PostMapping("/save")
    public String saveEmployee(@Valid @ModelAttribute("employee") Employee employee,
                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "employees/form"; // re-display form with validation error messages
        }
        employeeService.saveEmployee(employee);
        return "redirect:/employees"; // Post/Redirect/Get pattern - avoids duplicate form resubmission
    }

    /** DELETE: GET /employees/delete/{id} - removes the employee. */
    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return "redirect:/employees";
    }

    /** READ (single): GET /employees/view/{id} - shows one employee's
     *  full details plus their leave history. */
    @GetMapping("/view/{id}")
    public String viewEmployee(@PathVariable Long id, Model model) {
        model.addAttribute("employee", employeeService.getEmployeeById(id));
        return "employees/view";
    }
}
