package com.capgemini.apartment_maintenance.service;

import com.capgemini.apartment_maintenance.dto.*;
import com.capgemini.apartment_maintenance.entity.ComplaintStatus;
import java.util.List;

public interface ComplaintService {
    ComplaintResponseDTO createComplaint(ComplaintRequestDTO dto);
    List<ComplaintResponseDTO> getAllComplaints();
    ComplaintResponseDTO getComplaintById(Integer id);
    List<ComplaintResponseDTO> getComplaintsByResident(Integer residentId);
    List<ComplaintResponseDTO> getComplaintsByStaff(Integer staffId);
    List<ComplaintResponseDTO> getComplaintsByStatus(ComplaintStatus status);
    ComplaintResponseDTO assignStaff(Integer complaintId, Integer staffId);
    ComplaintResponseDTO updateStatus(Integer complaintId, ComplaintStatusUpdateDTO dto);
    void deleteComplaint(Integer id);
}