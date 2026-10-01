package com.internships.dao;

import com.internships.model.Skill;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SkillDaoImpl implements SkillDao {

    private final JdbcTemplate jdbcTemplate;

    public SkillDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Skill> SKILL_ROW_MAPPER = new RowMapper<Skill>() {
        @Override
        public Skill mapRow(ResultSet rs, int rowNum) throws SQLException {
            Skill s = new Skill();
            s.setSkillId(rs.getString("skill_id"));
            s.setSkillName(rs.getString("skill_name"));
            s.setCategory(rs.getString("category"));
            s.setCreatedAt(rs.getTimestamp("created_at").toString());
            return s;
        }
    };

    @Override
    public List<Skill> findAll() {
        String sql = "SELECT * FROM skills ORDER BY skill_name ASC";
        return jdbcTemplate.query(sql, SKILL_ROW_MAPPER);
    }

    @Override
    public Skill findById(String skillId) {
        String sql = "SELECT * FROM skills WHERE skill_id = ?";
        List<Skill> list = jdbcTemplate.query(sql, SKILL_ROW_MAPPER, skillId);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public Skill findByName(String skillName) {
        String sql = "SELECT * FROM skills WHERE skill_name = ?";
        List<Skill> list = jdbcTemplate.query(sql, SKILL_ROW_MAPPER, skillName);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public int save(Skill skill) {
        String sql = "INSERT INTO skills (skill_id, skill_name, category) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, skill.getSkillId(), skill.getSkillName(), skill.getCategory());
    }

    @Override
    public int deleteById(String skillId) {
        String sql = "DELETE FROM skills WHERE skill_id = ?";
        return jdbcTemplate.update(sql, skillId);
    }
}
