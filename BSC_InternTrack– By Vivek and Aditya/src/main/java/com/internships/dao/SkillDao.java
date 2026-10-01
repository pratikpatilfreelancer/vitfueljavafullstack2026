package com.internships.dao;

import com.internships.model.Skill;
import java.util.List;

public interface SkillDao {
    List<Skill> findAll();
    Skill findById(String skillId);
    Skill findByName(String skillName);
    int save(Skill skill);
    int deleteById(String skillId);
}
