package com.internships.service;

import com.internships.model.Company;
import java.util.List;

public interface CompanyService {
    List<Company> getAllCompanies();
    Company getCompanyById(String companyId);
    Company createCompany(Company company);
    Company updateCompany(String companyId, Company company);
    void deleteCompany(String companyId);
}
