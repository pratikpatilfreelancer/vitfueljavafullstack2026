package com.internships.service;

import com.internships.dao.SkillDao;
import com.internships.dao.StudentDao;
import com.internships.dao.StudentSkillDao;
import com.internships.exception.NotFoundException;
import com.internships.model.StudentSkill;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentSkillServiceImpl implements StudentSkillService {

    private final StudentSkillDao studentSkillDao;
    private final StudentDao studentDao;
    private final SkillDao skillDao;

    public StudentSkillServiceImpl(StudentSkillDao studentSkillDao, StudentDao studentDao, SkillDao skillDao) {
        this.studentSkillDao = studentSkillDao;
        this.studentDao = studentDao;
        this.skillDao = skillDao;
    }

    @Override
    public List<StudentSkill> getSkillsByStudentId(String studentId) {
        return studentSkillDao.findByStudentId(studentId);
    }

    @Override
    public List<StudentSkill> getStudentsBySkillId(String skillId) {
        return studentSkillDao.findBySkillId(skillId);
    }

    @Override
    public void addSkillToStudent(String studentId, String skillId) {
        if (studentDao.findById(studentId) == null) {
            throw new NotFoundException("Student not found with id: " + studentId);
        }
        if (skillDao.findById(skillId) == null) {
            throw new NotFoundException("Skill not found with id: " + skillId);
        }
        List<StudentSkill> existing = studentSkillDao.findByStudentId(studentId);
        boolean exists = existing.stream().anyMatch(ss -> ss.getSkillId().equals(skillId));
        if (!exists) {
            studentSkillDao.save(new StudentSkill(studentId, skillId, null));
        }
    }

    @Override
    public void removeSkillFromStudent(String studentId, String skillId) {
        studentSkillDao.delete(studentId, skillId);
    }

    @Override
    public void removeAllSkillsFromStudent(String studentId) {
        studentSkillDao.deleteByStudentId(studentId);
    }
}
