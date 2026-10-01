package com.internships.dao;

import com.internships.model.Internship;
import java.util.List;

public interface InternshipDao {
    List<Internship> findAll();
    Internship findById(String internshipId);
    List<Internship> findByCompanyId(String companyId);
    int save(Internship internship);
    int update(Internship internship);
    int deleteById(String internshipId);
}
