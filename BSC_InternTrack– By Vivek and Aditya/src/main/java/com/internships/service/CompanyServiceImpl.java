package com.internships.service;

import com.internships.dao.CompanyDao;
import com.internships.exception.NotFoundException;
import com.internships.model.Company;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyDao companyDao;

    public CompanyServiceImpl(CompanyDao companyDao) {
        this.companyDao = companyDao;
    }

    @Override
    public List<Company> getAllCompanies() {
        return companyDao.findAll();
    }

    @Override
    public Company getCompanyById(String companyId) {
        Company company = companyDao.findById(companyId);
        if (company == null) {
            throw new NotFoundException("Company not found with id: " + companyId);
        }
        return company;
    }

    @Override
    public Company createCompany(Company company) {
        if (company.getCompanyId() == null || company.getCompanyId().isBlank()) {
            throw new IllegalArgumentException("Company ID is required");
        }
        if (company.getCompanyName() == null || company.getCompanyName().isBlank()) {
            throw new IllegalArgumentException("Company name is required");
        }
        if (companyDao.findById(company.getCompanyId()) != null) {
            throw new IllegalArgumentException("Company with id already exists: " + company.getCompanyId());
        }
        companyDao.save(company);
        return companyDao.findById(company.getCompanyId());
    }

    @Override
    public Company updateCompany(String companyId, Company company) {
        Company existing = companyDao.findById(companyId);
        if (existing == null) {
            throw new NotFoundException("Company not found with id: " + companyId);
        }
        if (company.getCompanyName() == null || company.getCompanyName().isBlank()) {
            throw new IllegalArgumentException("Company name is required");
        }
        company.setCompanyId(companyId);
        companyDao.update(company);
        return companyDao.findById(companyId);
    }

    @Override
    public void deleteCompany(String companyId) {
        Company existing = companyDao.findById(companyId);
        if (existing == null) {
            throw new NotFoundException("Company not found with id: " + companyId);
        }
        companyDao.deleteById(companyId);
    }
}
