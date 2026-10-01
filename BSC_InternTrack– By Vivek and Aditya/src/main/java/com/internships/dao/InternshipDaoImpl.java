package com.internships.dao;

import com.internships.model.Internship;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class InternshipDaoImpl implements InternshipDao {

    private final JdbcTemplate jdbcTemplate;

    public InternshipDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Internship> INTERNSHIP_ROW_MAPPER = new RowMapper<Internship>() {
        @Override
        public Internship mapRow(ResultSet rs, int rowNum) throws SQLException {
            Internship i = new Internship();
            i.setInternshipId(rs.getString("internship_id"));
            i.setCompanyId(rs.getString("company_id"));
            i.setTitle(rs.getString("title"));
            i.setDescription(rs.getString("description"));
            i.setLocation(rs.getString("location"));
            i.setDuration(rs.getString("duration"));
            i.setStipend(rs.getDouble("stipend"));
            i.setApplicationDeadline(rs.getDate("application_deadline").toString());
            i.setCreatedAt(rs.getTimestamp("created_at").toString());
            return i;
        }
    };

    @Override
    public List<Internship> findAll() {
        String sql = "SELECT * FROM internships ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, INTERNSHIP_ROW_MAPPER);
    }

    @Override
    public Internship findById(String internshipId) {
        String sql = "SELECT * FROM internships WHERE internship_id = ?";
        List<Internship> list = jdbcTemplate.query(sql, INTERNSHIP_ROW_MAPPER, internshipId);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<Internship> findByCompanyId(String companyId) {
        String sql = "SELECT * FROM internships WHERE company_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, INTERNSHIP_ROW_MAPPER, companyId);
    }

    @Override
    public int save(Internship internship) {
        String sql = "INSERT INTO internships (internship_id, company_id, title, description, location, duration, stipend, application_deadline) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                internship.getInternshipId(),
                internship.getCompanyId(),
                internship.getTitle(),
                internship.getDescription(),
                internship.getLocation(),
                internship.getDuration(),
                internship.getStipend(),
                internship.getApplicationDeadline()
        );
    }

    @Override
    public int update(Internship internship) {
        String sql = "UPDATE internships SET company_id = ?, title = ?, description = ?, location = ?, duration = ?, stipend = ?, application_deadline = ? WHERE internship_id = ?";
        return jdbcTemplate.update(sql,
                internship.getCompanyId(),
                internship.getTitle(),
                internship.getDescription(),
                internship.getLocation(),
                internship.getDuration(),
                internship.getStipend(),
                internship.getApplicationDeadline(),
                internship.getInternshipId()
        );
    }

    @Override
    public int deleteById(String internshipId) {
        String sql = "DELETE FROM internships WHERE internship_id = ?";
        return jdbcTemplate.update(sql, internshipId);
    }
}
