package com.internships.dao;

import com.internships.model.StudentSkill;
import java.util.List;

public interface StudentSkillDao {
    List<StudentSkill> findByStudentId(String studentId);
    List<StudentSkill> findBySkillId(String skillId);
    int save(StudentSkill studentSkill);
    int delete(String studentId, String skillId);
    int deleteByStudentId(String studentId);
}
