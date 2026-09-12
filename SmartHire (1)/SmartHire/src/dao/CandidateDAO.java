package dao;

import model.Candidate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CandidateDAO {

    public int createCandidate(Candidate c) {
        String sql = "INSERT INTO candidates (user_id, phone, skills, education, experience_years, resume_text) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, c.getUserId());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getSkills());
            ps.setString(4, c.getEducation());
            ps.setInt(5, c.getExperienceYears());
            ps.setString(6, c.getResumeText());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updateCandidate(Candidate c) {
        String sql = "UPDATE candidates SET phone=?, skills=?, education=?, experience_years=?, resume_text=? "
                + "WHERE candidate_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getPhone());
            ps.setString(2, c.getSkills());
            ps.setString(3, c.getEducation());
            ps.setInt(4, c.getExperienceYears());
            ps.setString(5, c.getResumeText());
            ps.setInt(6, c.getCandidateId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Candidate getCandidateByUserId(int userId) {
        String sql = "SELECT * FROM candidates WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Candidate getCandidateById(int candidateId) {
        String sql = "SELECT * FROM candidates WHERE candidate_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, candidateId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Candidate> getAllCandidates() {
        List<Candidate> list = new ArrayList<>();
        String sql = "SELECT * FROM candidates";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Candidate mapRow(ResultSet rs) throws SQLException {
        Candidate c = new Candidate();
        c.setCandidateId(rs.getInt("candidate_id"));
        c.setUserId(rs.getInt("user_id"));
        c.setPhone(rs.getString("phone"));
        c.setSkills(rs.getString("skills"));
        c.setEducation(rs.getString("education"));
        c.setExperienceYears(rs.getInt("experience_years"));
        c.setResumeText(rs.getString("resume_text"));
        return c;
    }
}
