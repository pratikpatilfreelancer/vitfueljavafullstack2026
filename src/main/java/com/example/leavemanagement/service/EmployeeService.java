package com.example.leavemanagement.service;

import com.example.leavemanagement.entity.Employee;
import com.example.leavemanagement.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service (Business Logic Layer) for Employee-related operations.
 *
 * The Controller never talks to the Repository directly - it always goes
 * through the Service. This keeps the Controller focused only on
 * handling web requests, and keeps business rules in one place.
 */
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    // Constructor injection (preferred over @Autowired on fields) -
    // Spring automatically supplies the EmployeeRepository bean here.
    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /** CREATE / UPDATE: save() inserts a new row if id is null, or
     *  updates the existing row if id is already set. */
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    /** READ: fetch every employee, used for the employee list page. */
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    /** READ: fetch a single employee by id, used for view/edit pages. */
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    /** DELETE: remove an employee (and, thanks to cascade = ALL on the
     *  entity, their leave requests too) by id. */
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    /** Simple count used on the dashboard. */
    public long countEmployees() {
        return employeeRepository.count();
    }
}
