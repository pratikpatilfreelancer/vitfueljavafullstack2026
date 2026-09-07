package dao;

import model.Application;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDAO {

    public int createApplication(Application app) {
        String sql = "INSERT INTO applications (job_id, candidate_id, applied_date, status, match_score) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, app.getJobId());
            ps.setInt(2, app.getCandidateId());
            ps.setDate(3, Date.valueOf(app.getAppliedDate() == null ? LocalDate.now() : app.getAppliedDate()));
            ps.setString(4, app.getStatus() == null ? "APPLIED" : app.getStatus());
            ps.setDouble(5, app.getMatchScore());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updateStatus(int applicationId, String status) {
        String sql = "UPDATE applications SET status = ? WHERE application_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, applicationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMatchScore(int applicationId, double score) {
        String sql = "UPDATE applications SET match_score = ? WHERE application_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, score);
            ps.setInt(2, applicationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasApplied(int jobId, int candidateId) {
        String sql = "SELECT 1 FROM applications WHERE job_id = ? AND candidate_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            ps.setInt(2, candidateId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Applications submitted by one candidate, newest first, with job title joined in. */
    public List<Application> getApplicationsByCandidate(int candidateId) {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT a.*, j.title AS job_title FROM applications a "
                + "JOIN jobs j ON a.job_id = j.job_id "
                + "WHERE a.candidate_id = ? ORDER BY a.applied_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, candidateId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Application a = mapRow(rs);
                    a.setJobTitle(rs.getString("job_title"));
                    list.add(a);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Applicants for a given job, highest match score first, with candidate name joined in. */
    public List<Application> getApplicationsByJob(int jobId) {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT a.*, u.full_name AS candidate_name FROM applications a "
                + "JOIN candidates c ON a.candidate_id = c.candidate_id "
                + "JOIN users u ON c.user_id = u.user_id "
                + "WHERE a.job_id = ? ORDER BY a.match_score DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Application a = mapRow(rs);
                    a.setCandidateName(rs.getString("candidate_name"));
                    list.add(a);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** All applications for jobs owned by a recruiter — used for the reports screen. */
    public List<Application> getApplicationsByRecruiter(int recruiterId) {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT a.*, j.title AS job_title, u.full_name AS candidate_name FROM applications a "
                + "JOIN jobs j ON a.job_id = j.job_id "
                + "JOIN candidates c ON a.candidate_id = c.candidate_id "
                + "JOIN users u ON c.user_id = u.user_id "
                + "WHERE j.recruiter_id = ? ORDER BY a.applied_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recruiterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Application a = mapRow(rs);
                    a.setJobTitle(rs.getString("job_title"));
                    a.setCandidateName(rs.getString("candidate_name"));
                    list.add(a);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Application getApplicationById(int applicationId) {
        String sql = "SELECT * FROM applications WHERE application_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, applicationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Application mapRow(ResultSet rs) throws SQLException {
        Application a = new Application();
        a.setApplicationId(rs.getInt("application_id"));
        a.setJobId(rs.getInt("job_id"));
        a.setCandidateId(rs.getInt("candidate_id"));
        Date d = rs.getDate("applied_date");
        if (d != null) a.setAppliedDate(d.toLocalDate());
        a.setStatus(rs.getString("status"));
        a.setMatchScore(rs.getDouble("match_score"));
        return a;
    }
}
