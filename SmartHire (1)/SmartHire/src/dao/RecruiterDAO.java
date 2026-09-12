package dao;

import model.Recruiter;

import java.sql.*;

public class RecruiterDAO {

    public int createRecruiter(Recruiter r) {
        String sql = "INSERT INTO recruiters (user_id, company_name, department) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getUserId());
            ps.setString(2, r.getCompanyName());
            ps.setString(3, r.getDepartment());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Recruiter getRecruiterByUserId(int userId) {
        String sql = "SELECT * FROM recruiters WHERE user_id = ?";
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

    public Recruiter getRecruiterById(int recruiterId) {
        String sql = "SELECT * FROM recruiters WHERE recruiter_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recruiterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Recruiter mapRow(ResultSet rs) throws SQLException {
        Recruiter r = new Recruiter();
        r.setRecruiterId(rs.getInt("recruiter_id"));
        r.setUserId(rs.getInt("user_id"));
        r.setCompanyName(rs.getString("company_name"));
        r.setDepartment(rs.getString("department"));
        return r;
    }
}
