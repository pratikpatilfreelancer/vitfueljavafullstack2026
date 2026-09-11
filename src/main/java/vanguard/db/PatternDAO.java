package vanguard.db;

import vanguard.model.IncidentType;
import vanguard.model.Severity;
import vanguard.service.StoredPattern;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads/writes the incident_patterns reference table — the "case
 * base" that DatabaseBackedClassifier compares new reports against.
 */
public class PatternDAO {

    public List<StoredPattern> findAll() throws SQLException {
        String sql = "SELECT description, type, severity FROM incident_patterns";
        List<StoredPattern> patterns = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                patterns.add(new StoredPattern(
                        rs.getString("description"),
                        IncidentType.valueOf(rs.getString("type")),
                        Severity.valueOf(rs.getString("severity"))));
            }
        }
        return patterns;
    }

    public void insert(StoredPattern pattern) throws SQLException {
        String sql = "INSERT INTO incident_patterns (description, type, severity) VALUES (?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern.getDescription());
            ps.setString(2, pattern.getType().name());
            ps.setString(3, pattern.getSeverity().name());
            ps.executeUpdate();
        }
    }
}
