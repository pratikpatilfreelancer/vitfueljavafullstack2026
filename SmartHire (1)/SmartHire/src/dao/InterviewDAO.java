package dao;

import model.Interview;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InterviewDAO {

    public int scheduleInterview(Interview iv) {
        String sql = "INSERT INTO interviews (application_id, interview_date, interview_time, mode, status, feedback, rating) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, iv.getApplicationId());
            ps.setDate(2, Date.valueOf(iv.getInterviewDate()));
            ps.setTime(3, Time.valueOf(iv.getInterviewTime()));
            ps.setString(4, iv.getMode());
            ps.setString(5, iv.getStatus() == null ? "SCHEDULED" : iv.getStatus());
            ps.setString(6, iv.getFeedback());
            ps.setInt(7, iv.getRating());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updateInterview(Interview iv) {
        String sql = "UPDATE interviews SET interview_date=?, interview_time=?, mode=?, status=?, "
                + "feedback=?, rating=? WHERE interview_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(iv.getInterviewDate()));
            ps.setTime(2, Time.valueOf(iv.getInterviewTime()));
            ps.setString(3, iv.getMode());
            ps.setString(4, iv.getStatus());
            ps.setString(5, iv.getFeedback());
            ps.setInt(6, iv.getRating());
            ps.setInt(7, iv.getInterviewId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Interviews for jobs owned by a recruiter, joined with candidate name + job title. */
    public List<Interview> getInterviewsByRecruiter(int recruiterId) {
        List<Interview> list = new ArrayList<>();
        String sql = "SELECT i.*, u.full_name AS candidate_name, j.title AS job_title FROM interviews i "
                + "JOIN applications a ON i.application_id = a.application_id "
                + "JOIN jobs j ON a.job_id = j.job_id "
                + "JOIN candidates c ON a.candidate_id = c.candidate_id "
                + "JOIN users u ON c.user_id = u.user_id "
                + "WHERE j.recruiter_id = ? ORDER BY i.interview_date, i.interview_time";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recruiterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Interview iv = mapRow(rs);
                    iv.setCandidateName(rs.getString("candidate_name"));
                    iv.setJobTitle(rs.getString("job_title"));
                    list.add(iv);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Interviews for one candidate, joined with job title. */
    public List<Interview> getInterviewsByCandidate(int candidateId) {
        List<Interview> list = new ArrayList<>();
        String sql = "SELECT i.*, j.title AS job_title FROM interviews i "
                + "JOIN applications a ON i.application_id = a.application_id "
                + "JOIN jobs j ON a.job_id = j.job_id "
                + "WHERE a.candidate_id = ? ORDER BY i.interview_date, i.interview_time";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, candidateId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Interview iv = mapRow(rs);
                    iv.setJobTitle(rs.getString("job_title"));
                    list.add(iv);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Interview getInterviewByApplication(int applicationId) {
        String sql = "SELECT * FROM interviews WHERE application_id = ?";
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

    private Interview mapRow(ResultSet rs) throws SQLException {
        Interview iv = new Interview();
        iv.setInterviewId(rs.getInt("interview_id"));
        iv.setApplicationId(rs.getInt("application_id"));
        Date d = rs.getDate("interview_date");
        if (d != null) iv.setInterviewDate(d.toLocalDate());
        Time t = rs.getTime("interview_time");
        if (t != null) iv.setInterviewTime(t.toLocalTime());
        iv.setMode(rs.getString("mode"));
        iv.setStatus(rs.getString("status"));
        iv.setFeedback(rs.getString("feedback"));
        iv.setRating(rs.getInt("rating"));
        return iv;
    }
}
