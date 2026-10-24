package com.capgemini.apartment_maintenance.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.capgemini.apartment_maintenance.dto.ComplaintStatusUpdateDTO;
import com.capgemini.apartment_maintenance.entity.*;
import com.capgemini.apartment_maintenance.exception.InvalidOperationException;
import com.capgemini.apartment_maintenance.exception.ResourceNotFoundException;
import com.capgemini.apartment_maintenance.repository.*;

@ExtendWith(MockitoExtension.class)
class ComplaintServiceImplTest {

    @Mock private ComplaintRepository complaintRepository;
    @Mock private ResidentRepository residentRepository;
    @Mock private StaffRepository staffRepository;
    @Mock private ComplaintCategoryRepository categoryRepository;

    @InjectMocks private ComplaintServiceImpl service;

    private Resident resident;
    private ComplaintCategory category;
    private Staff staff;

    @BeforeEach
    void setUp() {
        resident = mock(Resident.class);
        category = mock(ComplaintCategory.class);
        staff = mock(Staff.class);
        lenient().when(resident.getName()).thenReturn("Asha");
        lenient().when(category.getCategoryName()).thenReturn("Plumbing");
        lenient().when(staff.getName()).thenReturn("Ravi");
    }

    private Complaint complaintWith(ComplaintStatus status, Staff assigned) {
        Complaint c = Complaint.builder()
                .description("Leaking tap")
                .status(status)
                .resident(resident)
                .category(category)
                .staff(assigned)
                .build();
        return c;
    }

    // ---------- status transitions ----------

    @Test
    void updateStatus_openToInProgress_succeeds() {
        Complaint c = complaintWith(ComplaintStatus.OPEN, null);
        when(complaintRepository.findById(1)).thenReturn(Optional.of(c));

        service.updateStatus(1, new ComplaintStatusUpdateDTO(ComplaintStatus.IN_PROGRESS));

        assertEquals(ComplaintStatus.IN_PROGRESS, c.getStatus());
    }

    @Test
    void updateStatus_inProgressToResolved_withStaff_setsResolvedDate() {
        Complaint c = complaintWith(ComplaintStatus.IN_PROGRESS, staff);
        when(complaintRepository.findById(1)).thenReturn(Optional.of(c));

        service.updateStatus(1, new ComplaintStatusUpdateDTO(ComplaintStatus.RESOLVED));

        assertEquals(ComplaintStatus.RESOLVED, c.getStatus());
        assertNotNull(c.getResolvedDate());
    }

    @Test
    void updateStatus_resolvedToClosed_succeeds() {
        Complaint c = complaintWith(ComplaintStatus.RESOLVED, staff);
        when(complaintRepository.findById(1)).thenReturn(Optional.of(c));

        service.updateStatus(1, new ComplaintStatusUpdateDTO(ComplaintStatus.CLOSED));

        assertEquals(ComplaintStatus.CLOSED, c.getStatus());
    }

    @ParameterizedTest
    @CsvSource({
        "OPEN, RESOLVED",
        "OPEN, CLOSED",
        "IN_PROGRESS, OPEN",
        "IN_PROGRESS, CLOSED",
        "RESOLVED, OPEN",
        "RESOLVED, IN_PROGRESS",
        "CLOSED, OPEN"
    })
    void updateStatus_invalidTransition_throwsAndKeepsStatus(ComplaintStatus from, ComplaintStatus to) {
        Complaint c = complaintWith(from, staff);
        when(complaintRepository.findById(1)).thenReturn(Optional.of(c));

        assertThrows(InvalidOperationException.class,
                () -> service.updateStatus(1, new ComplaintStatusUpdateDTO(to)));

        assertEquals(from, c.getStatus());
    }

    @Test
    void updateStatus_resolveWithoutStaff_throws() {
        Complaint c = complaintWith(ComplaintStatus.IN_PROGRESS, null);
        when(complaintRepository.findById(1)).thenReturn(Optional.of(c));

        assertThrows(InvalidOperationException.class,
                () -> service.updateStatus(1, new ComplaintStatusUpdateDTO(ComplaintStatus.RESOLVED)));

        assertEquals(ComplaintStatus.IN_PROGRESS, c.getStatus());
        assertNull(c.getResolvedDate());
    }

    @Test
    void updateStatus_unknownComplaint_throwsNotFound() {
        when(complaintRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.updateStatus(99, new ComplaintStatusUpdateDTO(ComplaintStatus.IN_PROGRESS)));
    }

    // ---------- assigning staff ----------

    @Test
    void assignStaff_onOpenComplaint_movesToInProgress() {
        Complaint c = complaintWith(ComplaintStatus.OPEN, null);
        when(complaintRepository.findById(1)).thenReturn(Optional.of(c));
        when(staffRepository.findById(5)).thenReturn(Optional.of(staff));

        service.assignStaff(1, 5);

        assertSame(staff, c.getStaff());
        assertEquals(ComplaintStatus.IN_PROGRESS, c.getStatus());
    }

    @Test
    void assignStaff_onResolvedComplaint_throws() {
        Complaint c = complaintWith(ComplaintStatus.RESOLVED, staff);
        when(complaintRepository.findById(1)).thenReturn(Optional.of(c));
        when(staffRepository.findById(5)).thenReturn(Optional.of(staff));

        assertThrows(InvalidOperationException.class, () -> service.assignStaff(1, 5));
    }

    @Test
    void assignStaff_unknownStaff_throwsNotFound() {
        Complaint c = complaintWith(ComplaintStatus.OPEN, null);
        when(complaintRepository.findById(1)).thenReturn(Optional.of(c));
        when(staffRepository.findById(404)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.assignStaff(1, 404));
        assertNull(c.getStaff());
    }

    // ---------- lookup and delete ----------

    @Test
    void getComplaintById_unknownId_throwsNotFound() {
        when(complaintRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getComplaintById(99));
    }

    @Test
    void deleteComplaint_existing_deletesIt() {
        Complaint c = complaintWith(ComplaintStatus.OPEN, null);
        when(complaintRepository.findById(1)).thenReturn(Optional.of(c));

        service.deleteComplaint(1);

        verify(complaintRepository).delete(c);
    }

    @Test
    void deleteComplaint_unknownId_throwsAndDeletesNothing() {
        when(complaintRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.deleteComplaint(99));
        verify(complaintRepository, never()).delete(any());
    }
}