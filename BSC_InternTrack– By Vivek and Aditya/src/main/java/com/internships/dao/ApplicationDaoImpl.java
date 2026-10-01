package com.internships.dao;

import com.internships.model.InternshipApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ApplicationDaoImpl implements ApplicationDao {

    private final JdbcTemplate jdbcTemplate;

    public ApplicationDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<InternshipApplication> APPLICATION_ROW_MAPPER = new RowMapper<InternshipApplication>() {
        @Override
        public InternshipApplication mapRow(ResultSet rs, int rowNum) throws SQLException {
            InternshipApplication app = new InternshipApplication();
            app.setApplicationId(rs.getString("application_id"));
            app.setStudentId(rs.getString("student_id"));
            app.setInternshipId(rs.getString("internship_id"));
            app.setApplicationDate(rs.getDate("application_date").toString());
            app.setStatus(rs.getString("status"));
            app.setNotes(rs.getString("notes"));
            app.setCreatedAt(rs.getTimestamp("created_at").toString());
            app.setUpdatedAt(rs.getTimestamp("updated_at").toString());
            return app;
        }
    };

    @Override
    public List<InternshipApplication> findAll() {
        String sql = "SELECT * FROM applications ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, APPLICATION_ROW_MAPPER);
    }

    @Override
    public InternshipApplication findById(String applicationId) {
        String sql = "SELECT * FROM applications WHERE application_id = ?";
        List<InternshipApplication> list = jdbcTemplate.query(sql, APPLICATION_ROW_MAPPER, applicationId);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<InternshipApplication> findByStudentId(String studentId) {
        String sql = "SELECT * FROM applications WHERE student_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, APPLICATION_ROW_MAPPER, studentId);
    }

    @Override
    public List<InternshipApplication> findByInternshipId(String internshipId) {
        String sql = "SELECT * FROM applications WHERE internship_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, APPLICATION_ROW_MAPPER, internshipId);
    }

    @Override
    public List<InternshipApplication> findByStatus(String status) {
        String sql = "SELECT * FROM applications WHERE status = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, APPLICATION_ROW_MAPPER, status);
    }

    @Override
    public int save(InternshipApplication application) {
        String sql = "INSERT INTO applications (application_id, student_id, internship_id, application_date, status, notes) VALUES (?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                application.getApplicationId(),
                application.getStudentId(),
                application.getInternshipId(),
                application.getApplicationDate(),
                application.getStatus(),
                application.getNotes()
        );
    }

    @Override
    public int update(InternshipApplication application) {
        String sql = "UPDATE applications SET student_id = ?, internship_id = ?, application_date = ?, status = ?, notes = ? WHERE application_id = ?";
        return jdbcTemplate.update(sql,
                application.getStudentId(),
                application.getInternshipId(),
                application.getApplicationDate(),
                application.getStatus(),
                application.getNotes(),
                application.getApplicationId()
        );
    }

    @Override
    public int updateStatus(String applicationId, String status) {
        String sql = "UPDATE applications SET status = ? WHERE application_id = ?";
        return jdbcTemplate.update(sql, status, applicationId);
    }

    @Override
    public int deleteById(String applicationId) {
        String sql = "DELETE FROM applications WHERE application_id = ?";
        return jdbcTemplate.update(sql, applicationId);
    }

    @Override
    public long countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM applications WHERE status = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, status);
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM applications";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
}
