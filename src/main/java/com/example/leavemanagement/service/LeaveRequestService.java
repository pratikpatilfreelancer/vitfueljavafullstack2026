package com.example.leavemanagement.service;

import com.example.leavemanagement.entity.LeaveRequest;
import com.example.leavemanagement.entity.LeaveStatus;
import com.example.leavemanagement.repository.LeaveRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service (Business Logic Layer) for Leave Request operations:
 * applying for leave, viewing requests, and approving/rejecting them.
 */
@Service
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;

    @Autowired
    public LeaveRequestService(LeaveRequestRepository leaveRequestRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
    }

    /** CREATE: an employee applies for leave. Status defaults to PENDING
     *  (set inside the LeaveRequest entity/constructor). */
    public LeaveRequest applyForLeave(LeaveRequest leaveRequest) {
        return leaveRequestRepository.save(leaveRequest);
    }

    /** READ: all leave requests, newest logic can be sorted in the
     *  controller/template if needed - kept simple here. */
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAll();
    }

    /** READ: a single leave request by id (used before approve/reject). */
    public LeaveRequest getLeaveRequestById(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave request not found with id: " + id));
    }

    /** READ: only PENDING requests - used on the dashboard's "awaiting
     *  approval" widget. */
    public List<LeaveRequest> getPendingLeaveRequests() {
        return leaveRequestRepository.findByStatus(LeaveStatus.PENDING);
    }

    /** UPDATE: change a leave request's status to APPROVED. */
    public void approveLeaveRequest(Long id) {
        LeaveRequest leaveRequest = getLeaveRequestById(id);
        leaveRequest.setStatus(LeaveStatus.APPROVED);
        leaveRequestRepository.save(leaveRequest);
    }

    /** UPDATE: change a leave request's status to REJECTED. */
    public void rejectLeaveRequest(Long id) {
        LeaveRequest leaveRequest = getLeaveRequestById(id);
        leaveRequest.setStatus(LeaveStatus.REJECTED);
        leaveRequestRepository.save(leaveRequest);
    }

    /** DELETE: remove a leave request entirely. */
    public void deleteLeaveRequest(Long id) {
        leaveRequestRepository.deleteById(id);
    }

    /** Simple counts used on the dashboard. */
    public long countAllLeaveRequests() {
        return leaveRequestRepository.count();
    }

    public long countPendingLeaveRequests() {
        return leaveRequestRepository.findByStatus(LeaveStatus.PENDING).size();
    }

    public long countApprovedLeaveRequests() {
        return leaveRequestRepository.findByStatus(LeaveStatus.APPROVED).size();
    }

    public long countRejectedLeaveRequests() {
        return leaveRequestRepository.findByStatus(LeaveStatus.REJECTED).size();
    }
}
