package com.internships.service;

import com.internships.dao.CompanyDao;
import com.internships.dao.InternshipDao;
import com.internships.exception.NotFoundException;
import com.internships.model.Internship;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InternshipServiceImpl implements InternshipService {

    private final InternshipDao internshipDao;
    private final CompanyDao companyDao;

    public InternshipServiceImpl(InternshipDao internshipDao, CompanyDao companyDao) {
        this.internshipDao = internshipDao;
        this.companyDao = companyDao;
    }

    @Override
    public List<Internship> getAllInternships() {
        return internshipDao.findAll();
    }

    @Override
    public Internship getInternshipById(String internshipId) {
        Internship internship = internshipDao.findById(internshipId);
        if (internship == null) {
            throw new NotFoundException("Internship not found with id: " + internshipId);
        }
        return internship;
    }

    @Override
    public List<Internship> getInternshipsByCompanyId(String companyId) {
        return internshipDao.findByCompanyId(companyId);
    }

    @Override
    public Internship createInternship(Internship internship) {
        if (internship.getInternshipId() == null || internship.getInternshipId().isBlank()) {
            throw new IllegalArgumentException("Internship ID is required");
        }
        if (internship.getTitle() == null || internship.getTitle().isBlank()) {
            throw new IllegalArgumentException("Internship title is required");
        }
        if (internship.getCompanyId() == null || internship.getCompanyId().isBlank()) {
            throw new IllegalArgumentException("Company ID is required");
        }
        if (companyDao.findById(internship.getCompanyId()) == null) {
            throw new NotFoundException("Company not found with id: " + internship.getCompanyId());
        }
        if (internshipDao.findById(internship.getInternshipId()) != null) {
            throw new IllegalArgumentException("Internship with id already exists: " + internship.getInternshipId());
        }
        internshipDao.save(internship);
        return internshipDao.findById(internship.getInternshipId());
    }

    @Override
    public Internship updateInternship(String internshipId, Internship internship) {
        Internship existing = internshipDao.findById(internshipId);
        if (existing == null) {
            throw new NotFoundException("Internship not found with id: " + internshipId);
        }
        if (internship.getCompanyId() == null || internship.getCompanyId().isBlank()) {
            throw new IllegalArgumentException("Company ID is required");
        }
        if (companyDao.findById(internship.getCompanyId()) == null) {
            throw new NotFoundException("Company not found with id: " + internship.getCompanyId());
        }
        internship.setInternshipId(internshipId);
        internshipDao.update(internship);
        return internshipDao.findById(internshipId);
    }

    @Override
    public void deleteInternship(String internshipId) {
        Internship existing = internshipDao.findById(internshipId);
        if (existing == null) {
            throw new NotFoundException("Internship not found with id: " + internshipId);
        }
        internshipDao.deleteById(internshipId);
    }
}
