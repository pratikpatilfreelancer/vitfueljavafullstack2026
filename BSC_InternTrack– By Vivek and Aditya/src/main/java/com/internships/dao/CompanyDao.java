package com.internships.dao;

import com.internships.model.Company;
import java.util.List;

public interface CompanyDao {
    List<Company> findAll();
    Company findById(String companyId);
    int save(Company company);
    int update(Company company);
    int deleteById(String companyId);
}
