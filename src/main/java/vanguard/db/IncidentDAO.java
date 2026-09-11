package vanguard.db;

import vanguard.model.Incident;
import vanguard.model.IncidentStatus;
import vanguard.model.IncidentType;
import vanguard.model.Severity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the `incidents` table. Every method opens
 * its own Connection/PreparedStatement inside try-with-resources, so
 * nothing is left open even if a SQLException is thrown mid-query.
 * This is the practical, hands-on demonstration of the SQL/JDBC
 * portion of the syllabus: INSERT, SELECT ... WHERE, UPDATE, DELETE,
 * and an INNER JOIN query, all backed by PreparedStatement to avoid
 * SQL injection.
 */
public class IncidentDAO {

    /**
     * Insert-or-update: safe to call repeatedly for the same incident
     * id (e.g. every time the GUI's "Save to MySQL" button is clicked)
     * without throwing a duplicate-key error.
     */
    public void save(Incident incident) throws SQLException {
        String sql = "INSERT INTO incidents (id, description, x, y, type, severity, "
                + "confidence, status, assigned_resource_id) VALUES (?,?,?,?,?,?,?,?,?) "
                + "ON DUPLICATE KEY UPDATE description=VALUES(description), x=VALUES(x), "
                + "y=VALUES(y), type=VALUES(type), severity=VALUES(severity), "
                + "confidence=VALUES(confidence), status=VALUES(status), "
                + "assigned_resource_id=VALUES(assigned_resource_id)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, incident.getId());
            ps.setString(2, incident.getDescription());
            ps.setInt(3, incident.getX());
            ps.setInt(4, incident.getY());
            ps.setString(5, incident.getType().name());
            ps.setString(6, incident.getSeverity().name());
            ps.setInt(7, incident.getConfidence());
            ps.setString(8, incident.getStatus().name());
            ps.setString(9, incident.getAssignedResourceId());
            ps.executeUpdate();
        }
    }

    public List<Incident> findAll() throws SQLException {
        String sql = "SELECT * FROM incidents";
        List<Incident> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        }
        return results;
    }

    public List<Incident> findBySeverity(Severity severity) throws SQLException {
        String sql = "SELECT * FROM incidents WHERE severity = ?";
        List<Incident> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, severity.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

    public void updateStatus(String incidentId, IncidentStatus status) throws SQLException {
        String sql = "UPDATE incidents SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, incidentId);
            ps.executeUpdate();
        }
    }

    public void delete(String incidentId) throws SQLException {
        String sql = "DELETE FROM incidents WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, incidentId);
            ps.executeUpdate();
        }
    }

    /**
     * INNER JOIN example: every assigned incident paired with the
     * resource actually assigned to it, reading resource name/type
     * straight from the `resources` table in one query.
     */
    public void printAssignmentsJoin() throws SQLException {
        String sql = "SELECT i.id AS incident_id, i.description, r.name AS resource_name, "
                + "r.type AS resource_type "
                + "FROM incidents i "
                + "INNER JOIN resources r ON i.assigned_resource_id = r.id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                System.out.printf("%s (%s) -> %s [%s]%n",
                        rs.getString("incident_id"),
                        rs.getString("description"),
                        rs.getString("resource_name"),
                        rs.getString("resource_type"));
            }
        }
    }

    private Incident mapRow(ResultSet rs) throws SQLException {
        Incident incident = new Incident(
                rs.getString("id"),
                rs.getString("description"),
                rs.getInt("x"),
                rs.getInt("y"));
        incident.setType(IncidentType.valueOf(rs.getString("type")));
        incident.setSeverity(Severity.valueOf(rs.getString("severity")));
        incident.setConfidence(rs.getInt("confidence"));
        incident.setStatus(IncidentStatus.valueOf(rs.getString("status")));
        incident.setAssignedResourceId(rs.getString("assigned_resource_id"));
        return incident;
    }
}
