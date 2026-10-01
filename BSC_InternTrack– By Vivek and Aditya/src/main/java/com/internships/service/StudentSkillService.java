package com.internships.service;

import com.internships.model.StudentSkill;
import java.util.List;

public interface StudentSkillService {
    List<StudentSkill> getSkillsByStudentId(String studentId);
    List<StudentSkill> getStudentsBySkillId(String skillId);
    void addSkillToStudent(String studentId, String skillId);
    void removeSkillFromStudent(String studentId, String skillId);
    void removeAllSkillsFromStudent(String studentId);
}
