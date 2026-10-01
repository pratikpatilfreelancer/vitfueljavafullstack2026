package com.internships.dao;

import com.internships.model.InternshipApplication;
import java.util.List;

public interface ApplicationDao {
    List<InternshipApplication> findAll();
    InternshipApplication findById(String applicationId);
    List<InternshipApplication> findByStudentId(String studentId);
    List<InternshipApplication> findByInternshipId(String internshipId);
    List<InternshipApplication> findByStatus(String status);
    int save(InternshipApplication application);
    int update(InternshipApplication application);
    int updateStatus(String applicationId, String status);
    int deleteById(String applicationId);
    long countByStatus(String status);
    long countAll();
}
