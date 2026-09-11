package vanguard.db;

import vanguard.model.IncidentType;
import vanguard.service.KeywordRule;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Reads/writes the keyword_rules table — the SQL-backed keyword dataset. */
public class KeywordDAO {

    public List<KeywordRule> findAll() throws SQLException {
        String sql = "SELECT keyword, type, severity_weight FROM keyword_rules";
        List<KeywordRule> rules = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String typeStr = rs.getString("type");
                IncidentType type = typeStr == null ? null : IncidentType.valueOf(typeStr);
                rules.add(new KeywordRule(rs.getString("keyword"), type, rs.getInt("severity_weight")));
            }
        }
        return rules;
    }

    public void insert(KeywordRule rule) throws SQLException {
        String sql = "INSERT INTO keyword_rules (keyword, type, severity_weight) VALUES (?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rule.getKeyword());
            if (rule.getType() == null) {
                ps.setNull(2, Types.VARCHAR);
            } else {
                ps.setString(2, rule.getType().name());
            }
            ps.setInt(3, rule.getSeverityWeight());
            ps.executeUpdate();
        }
    }
}
