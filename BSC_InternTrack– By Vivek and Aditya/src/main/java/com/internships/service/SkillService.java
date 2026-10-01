package com.internships.service;

import com.internships.model.Skill;
import java.util.List;

public interface SkillService {
    List<Skill> getAllSkills();
    Skill getSkillById(String skillId);
    Skill getSkillByName(String skillName);
    Skill createSkill(Skill skill);
    void deleteSkill(String skillId);
}
