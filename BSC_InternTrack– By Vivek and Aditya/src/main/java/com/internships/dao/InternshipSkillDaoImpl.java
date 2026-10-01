package com.internships.dao;

import com.internships.model.InternshipSkill;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class InternshipSkillDaoImpl implements InternshipSkillDao {

    private final JdbcTemplate jdbcTemplate;

    public InternshipSkillDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<InternshipSkill> INTERNSHIP_SKILL_ROW_MAPPER = new RowMapper<InternshipSkill>() {
        @Override
        public InternshipSkill mapRow(ResultSet rs, int rowNum) throws SQLException {
            InternshipSkill is = new InternshipSkill();
            is.setInternshipId(rs.getString("internship_id"));
            is.setSkillId(rs.getString("skill_id"));
            is.setCreatedAt(rs.getTimestamp("created_at").toString());
            return is;
        }
    };

    @Override
    public List<InternshipSkill> findByInternshipId(String internshipId) {
        String sql = "SELECT * FROM internship_skills WHERE internship_id = ? ORDER BY created_at ASC";
        return jdbcTemplate.query(sql, INTERNSHIP_SKILL_ROW_MAPPER, internshipId);
    }

    @Override
    public List<InternshipSkill> findBySkillId(String skillId) {
        String sql = "SELECT * FROM internship_skills WHERE skill_id = ? ORDER BY created_at ASC";
        return jdbcTemplate.query(sql, INTERNSHIP_SKILL_ROW_MAPPER, skillId);
    }

    @Override
    public int save(InternshipSkill internshipSkill) {
        String sql = "INSERT INTO internship_skills (internship_id, skill_id) VALUES (?, ?)";
        return jdbcTemplate.update(sql, internshipSkill.getInternshipId(), internshipSkill.getSkillId());
    }

    @Override
    public int delete(String internshipId, String skillId) {
        String sql = "DELETE FROM internship_skills WHERE internship_id = ? AND skill_id = ?";
        return jdbcTemplate.update(sql, internshipId, skillId);
    }

    @Override
    public int deleteByInternshipId(String internshipId) {
        String sql = "DELETE FROM internship_skills WHERE internship_id = ?";
        return jdbcTemplate.update(sql, internshipId);
    }
}
