package com.internships.dao;

import com.internships.model.StudentSkill;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class StudentSkillDaoImpl implements StudentSkillDao {

    private final JdbcTemplate jdbcTemplate;

    public StudentSkillDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<StudentSkill> STUDENT_SKILL_ROW_MAPPER = new RowMapper<StudentSkill>() {
        @Override
        public StudentSkill mapRow(ResultSet rs, int rowNum) throws SQLException {
            StudentSkill ss = new StudentSkill();
            ss.setStudentId(rs.getString("student_id"));
            ss.setSkillId(rs.getString("skill_id"));
            ss.setCreatedAt(rs.getTimestamp("created_at").toString());
            return ss;
        }
    };

    @Override
    public List<StudentSkill> findByStudentId(String studentId) {
        String sql = "SELECT * FROM student_skills WHERE student_id = ? ORDER BY created_at ASC";
        return jdbcTemplate.query(sql, STUDENT_SKILL_ROW_MAPPER, studentId);
    }

    @Override
    public List<StudentSkill> findBySkillId(String skillId) {
        String sql = "SELECT * FROM student_skills WHERE skill_id = ? ORDER BY created_at ASC";
        return jdbcTemplate.query(sql, STUDENT_SKILL_ROW_MAPPER, skillId);
    }

    @Override
    public int save(StudentSkill studentSkill) {
        String sql = "INSERT INTO student_skills (student_id, skill_id) VALUES (?, ?)";
        return jdbcTemplate.update(sql, studentSkill.getStudentId(), studentSkill.getSkillId());
    }

    @Override
    public int delete(String studentId, String skillId) {
        String sql = "DELETE FROM student_skills WHERE student_id = ? AND skill_id = ?";
        return jdbcTemplate.update(sql, studentId, skillId);
    }

    @Override
    public int deleteByStudentId(String studentId) {
        String sql = "DELETE FROM student_skills WHERE student_id = ?";
        return jdbcTemplate.update(sql, studentId);
    }
}
