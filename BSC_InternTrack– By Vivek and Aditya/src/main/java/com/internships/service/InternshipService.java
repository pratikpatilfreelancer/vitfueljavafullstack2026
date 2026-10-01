package com.internships.service;

import com.internships.model.Internship;
import java.util.List;

public interface InternshipService {
    List<Internship> getAllInternships();
    Internship getInternshipById(String internshipId);
    List<Internship> getInternshipsByCompanyId(String companyId);
    Internship createInternship(Internship internship);
    Internship updateInternship(String internshipId, Internship internship);
    void deleteInternship(String internshipId);
}
