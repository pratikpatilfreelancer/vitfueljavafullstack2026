package com.internships.service;

import com.internships.dao.InternshipDao;
import com.internships.dao.InternshipSkillDao;
import com.internships.dao.SkillDao;
import com.internships.exception.NotFoundException;
import com.internships.model.InternshipSkill;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InternshipSkillServiceImpl implements InternshipSkillService {

    private final InternshipSkillDao internshipSkillDao;
    private final InternshipDao internshipDao;
    private final SkillDao skillDao;

    public InternshipSkillServiceImpl(InternshipSkillDao internshipSkillDao, InternshipDao internshipDao, SkillDao skillDao) {
        this.internshipSkillDao = internshipSkillDao;
        this.internshipDao = internshipDao;
        this.skillDao = skillDao;
    }

    @Override
    public List<InternshipSkill> getSkillsByInternshipId(String internshipId) {
        return internshipSkillDao.findByInternshipId(internshipId);
    }

    @Override
    public List<InternshipSkill> getInternshipsBySkillId(String skillId) {
        return internshipSkillDao.findBySkillId(skillId);
    }

    @Override
    public void addSkillToInternship(String internshipId, String skillId) {
        if (internshipDao.findById(internshipId) == null) {
            throw new NotFoundException("Internship not found with id: " + internshipId);
        }
        if (skillDao.findById(skillId) == null) {
            throw new NotFoundException("Skill not found with id: " + skillId);
        }
        List<InternshipSkill> existing = internshipSkillDao.findByInternshipId(internshipId);
        boolean exists = existing.stream().anyMatch(is -> is.getSkillId().equals(skillId));
        if (!exists) {
            internshipSkillDao.save(new InternshipSkill(internshipId, skillId, null));
        }
    }

    @Override
    public void removeSkillFromInternship(String internshipId, String skillId) {
        internshipSkillDao.delete(internshipId, skillId);
    }

    @Override
    public void removeAllSkillsFromInternship(String internshipId) {
        internshipSkillDao.deleteByInternshipId(internshipId);
    }
}
