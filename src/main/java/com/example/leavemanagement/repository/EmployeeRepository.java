package com.example.leavemanagement.repository;

import com.example.leavemanagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository (Data Access Layer) for the Employee entity.
 *
 * By extending JpaRepository<Employee, Long>, Spring Data JPA
 * automatically generates the implementation for us at runtime, giving
 * us methods like save(), findById(), findAll(), deleteById(), etc.
 * without writing a single line of SQL or implementation code.
 *
 * Long = the type of Employee's primary key (id).
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // No extra methods needed yet - JpaRepository already covers
    // all the CRUD operations this project requires.
}
