package vanguard.db;

import vanguard.model.IncidentType;
import vanguard.model.Resource;
import vanguard.model.ResourceStatus;
import vanguard.util.ResourceFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResourceDAO {

    /** Insert-or-update, same reasoning as IncidentDAO.save(). */
    public void save(Resource resource) throws SQLException {
        String sql = "INSERT INTO resources (id, name, type, x, y, status) VALUES (?,?,?,?,?,?) "
                + "ON DUPLICATE KEY UPDATE name=VALUES(name), type=VALUES(type), "
                + "x=VALUES(x), y=VALUES(y), status=VALUES(status)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, resource.getId());
            ps.setString(2, resource.getName());
            ps.setString(3, resource.getSpecialty().name());
            ps.setInt(4, resource.getX());
            ps.setInt(5, resource.getY());
            ps.setString(6, resource.getStatus().name());
            ps.executeUpdate();
        }
    }

    public List<Resource> findAll() throws SQLException {
        String sql = "SELECT * FROM resources";
        List<Resource> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                IncidentType type = IncidentType.valueOf(rs.getString("type"));
                Resource resource = ResourceFactory.create(
                        type, rs.getString("id"), rs.getString("name"),
                        rs.getInt("x"), rs.getInt("y"));
                if (ResourceStatus.valueOf(rs.getString("status")) == ResourceStatus.BUSY) {
                    resource.markBusy();
                }
                results.add(resource);
            }
        }
        return results;
    }

    public void updateStatus(String resourceId, ResourceStatus status) throws SQLException {
        String sql = "UPDATE resources SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, resourceId);
            ps.executeUpdate();
        }
    }

    public void delete(String resourceId) throws SQLException {
        String sql = "DELETE FROM resources WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, resourceId);
            ps.executeUpdate();
        }
    }
}
