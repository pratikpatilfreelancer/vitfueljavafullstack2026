package com.example.leavemanagement.repository;

import com.example.leavemanagement.entity.LeaveRequest;
import com.example.leavemanagement.entity.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository (Data Access Layer) for the LeaveRequest entity.
 *
 * Beyond the free CRUD methods from JpaRepository, we declare two
 * "derived query methods". Spring Data JPA reads the method NAME itself
 * and automatically builds the correct SQL query behind the scenes -
 * no @Query annotation or SQL needed for simple lookups like these.
 */
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    /**
     * Generates: SELECT * FROM leave_requests WHERE status = ?
     * Used on the dashboard/leave list to filter by Pending/Approved/Rejected.
     */
    List<LeaveRequest> findByStatus(LeaveStatus status);

    /**
     * Generates a JOIN query behind the scenes:
     * SELECT * FROM leave_requests WHERE employee_id = ?
     * Used to show "leave history" for one specific employee.
     */
    List<LeaveRequest> findByEmployeeId(Long employeeId);
}
