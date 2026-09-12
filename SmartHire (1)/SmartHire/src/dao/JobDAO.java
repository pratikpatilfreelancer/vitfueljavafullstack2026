package dao;

import model.Job;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JobDAO {

    public int createJob(Job job) {
        String sql = "INSERT INTO jobs (recruiter_id, title, description, required_skills, "
                + "min_experience, location, status, posted_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, job.getRecruiterId());
            ps.setString(2, job.getTitle());
            ps.setString(3, job.getDescription());
            ps.setString(4, job.getRequiredSkills());
            ps.setInt(5, job.getMinExperience());
            ps.setString(6, job.getLocation());
            ps.setString(7, job.getStatus() == null ? "OPEN" : job.getStatus());
            ps.setDate(8, Date.valueOf(job.getPostedDate() == null ? LocalDate.now() : job.getPostedDate()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updateJob(Job job) {
        String sql = "UPDATE jobs SET title=?, description=?, required_skills=?, min_experience=?, "
                + "location=?, status=? WHERE job_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, job.getTitle());
            ps.setString(2, job.getDescription());
            ps.setString(3, job.getRequiredSkills());
            ps.setInt(4, job.getMinExperience());
            ps.setString(5, job.getLocation());
            ps.setString(6, job.getStatus());
            ps.setInt(7, job.getJobId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteJob(int jobId) {
        String sql = "DELETE FROM jobs WHERE job_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Job getJobById(int jobId) {
        String sql = "SELECT * FROM jobs WHERE job_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Job> getJobsByRecruiter(int recruiterId) {
        List<Job> list = new ArrayList<>();
        String sql = "SELECT * FROM jobs WHERE recruiter_id = ? ORDER BY posted_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recruiterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Job> getAllOpenJobs() {
        List<Job> list = new ArrayList<>();
        String sql = "SELECT * FROM jobs WHERE status = 'OPEN' ORDER BY posted_date DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Job> searchJobs(String keyword) {
        List<Job> list = new ArrayList<>();
        String sql = "SELECT * FROM jobs WHERE status = 'OPEN' AND "
                + "(title LIKE ? OR required_skills LIKE ? OR location LIKE ?) ORDER BY posted_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Job mapRow(ResultSet rs) throws SQLException {
        Job j = new Job();
        j.setJobId(rs.getInt("job_id"));
        j.setRecruiterId(rs.getInt("recruiter_id"));
        j.setTitle(rs.getString("title"));
        j.setDescription(rs.getString("description"));
        j.setRequiredSkills(rs.getString("required_skills"));
        j.setMinExperience(rs.getInt("min_experience"));
        j.setLocation(rs.getString("location"));
        j.setStatus(rs.getString("status"));
        Date d = rs.getDate("posted_date");
        if (d != null) j.setPostedDate(d.toLocalDate());
        return j;
    }
}
