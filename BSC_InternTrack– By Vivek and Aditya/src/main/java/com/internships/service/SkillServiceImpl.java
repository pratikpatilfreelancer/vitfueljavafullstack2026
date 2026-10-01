package com.internships.service;

import com.internships.dao.SkillDao;
import com.internships.exception.DuplicateResourceException;
import com.internships.exception.NotFoundException;
import com.internships.model.Skill;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillServiceImpl implements SkillService {

    private final SkillDao skillDao;

    public SkillServiceImpl(SkillDao skillDao) {
        this.skillDao = skillDao;
    }

    @Override
    public List<Skill> getAllSkills() {
        return skillDao.findAll();
    }

    @Override
    public Skill getSkillById(String skillId) {
        Skill skill = skillDao.findById(skillId);
        if (skill == null) {
            throw new NotFoundException("Skill not found with id: " + skillId);
        }
        return skill;
    }

    @Override
    public Skill getSkillByName(String skillName) {
        Skill skill = skillDao.findByName(skillName);
        if (skill == null) {
            throw new NotFoundException("Skill not found with name: " + skillName);
        }
        return skill;
    }

    @Override
    public Skill createSkill(Skill skill) {
        if (skill.getSkillName() == null || skill.getSkillName().isBlank()) {
            throw new IllegalArgumentException("Skill name is required");
        }
        if (skillDao.findByName(skill.getSkillName()) != null) {
            throw new DuplicateResourceException("Skill already exists with name: " + skill.getSkillName());
        }
        skillDao.save(skill);
        return skillDao.findByName(skill.getSkillName());
    }

    @Override
    public void deleteSkill(String skillId) {
        Skill skill = skillDao.findById(skillId);
        if (skill == null) {
            throw new NotFoundException("Skill not found with id: " + skillId);
        }
        skillDao.deleteById(skillId);
    }
}
