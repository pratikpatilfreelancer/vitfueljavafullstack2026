package com.capgemini.apartment_maintenance.repository;

import com.capgemini.apartment_maintenance.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for Staff entity.
 * Supports filtering staff by specialization for complaint assignment.
 */
public interface StaffRepository extends JpaRepository<Staff, Integer> {

    // e.g. find all "Plumbing" staff to assign a plumbing complaint
    List<Staff> findBySpecialization(String specialization);

    List<Staff> findByDesignation(String designation);
}