package com.internships.service;

import com.internships.model.InternshipApplication;
import java.util.List;

public interface ApplicationService {
    List<InternshipApplication> getAllApplications();
    InternshipApplication getApplicationById(String applicationId);
    List<InternshipApplication> getApplicationsByStudentId(String studentId);
    List<InternshipApplication> getApplicationsByInternshipId(String internshipId);
    List<InternshipApplication> getApplicationsByStatus(String status);
    InternshipApplication createApplication(InternshipApplication application);
    InternshipApplication updateApplication(String applicationId, InternshipApplication application);
    InternshipApplication updateApplicationStatus(String applicationId, String status);
    void deleteApplication(String applicationId);
    long countByStatus(String status);
    long countAll();
}
