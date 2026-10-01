package com.internships.dao;

import com.internships.model.InternshipSkill;
import java.util.List;

public interface InternshipSkillDao {
    List<InternshipSkill> findByInternshipId(String internshipId);
    List<InternshipSkill> findBySkillId(String skillId);
    int save(InternshipSkill internshipSkill);
    int delete(String internshipId, String skillId);
    int deleteByInternshipId(String internshipId);
}
