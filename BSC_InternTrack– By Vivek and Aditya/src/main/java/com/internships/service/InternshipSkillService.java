package com.internships.service;

import com.internships.model.InternshipSkill;
import java.util.List;

public interface InternshipSkillService {
    List<InternshipSkill> getSkillsByInternshipId(String internshipId);
    List<InternshipSkill> getInternshipsBySkillId(String skillId);
    void addSkillToInternship(String internshipId, String skillId);
    void removeSkillFromInternship(String internshipId, String skillId);
    void removeAllSkillsFromInternship(String internshipId);
}
