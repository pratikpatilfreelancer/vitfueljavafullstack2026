package com.capgemini.apartment_maintenance.repository;

import com.capgemini.apartment_maintenance.entity.Complaint;
import com.capgemini.apartment_maintenance.entity.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for Complaint entity — the core entity of the system.
 * Provides derived queries for status filtering, resident history,
 * and staff assignment views used across the CRUD + dashboard endpoints.
 */
public interface ComplaintRepository extends JpaRepository<Complaint, Integer> {

    // note: status is an enum (ComplaintStatus), not a String, since
    // Complaint.status is mapped with @Enumerated(EnumType.STRING)
    List<Complaint> findByStatus(ComplaintStatus status);

    // view history for a specific resident, most recent first
    List<Complaint> findByResident_ResidentIdOrderByCreatedDateDesc(Integer residentId);

    // complaints currently assigned to a staff member
    List<Complaint> findByStaff_StaffId(Integer staffId);

    // complaints assigned to a staff member, filtered by status
    // (e.g. staff dashboard: "my open complaints")
    List<Complaint> findByStaff_StaffIdAndStatus(Integer staffId, ComplaintStatus status);

    // all complaints under a given category
    List<Complaint> findByCategory_CategoryId(Integer categoryId);

    // unassigned complaints — staff is null (ON DELETE SET NULL scenario too)
    List<Complaint> findByStaffIsNull();

    // combined filter for admin dashboard: status + category
    List<Complaint> findByStatusAndCategory_CategoryId(ComplaintStatus status, Integer categoryId);
}