package com.internships.dao;

import com.internships.model.Notification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class NotificationDaoImpl implements NotificationDao {

    private final JdbcTemplate jdbcTemplate;

    public NotificationDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Notification> NOTIFICATION_ROW_MAPPER = new RowMapper<Notification>() {
        @Override
        public Notification mapRow(ResultSet rs, int rowNum) throws SQLException {
            Notification n = new Notification();
            n.setNotificationId(rs.getString("notification_id"));
            n.setUserId(rs.getString("user_id"));
            n.setMessage(rs.getString("message"));
            n.setType(rs.getString("type"));
            n.setRead(rs.getBoolean("is_read"));
            n.setCreatedAt(rs.getTimestamp("created_at").toString());
            return n;
        }
    };

    @Override
    public List<Notification> findByUserId(String userId) {
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, NOTIFICATION_ROW_MAPPER, userId);
    }

    @Override
    public List<Notification> findByUserIdAndIsRead(String userId, boolean isRead) {
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND is_read = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, NOTIFICATION_ROW_MAPPER, userId, isRead);
    }

    @Override
    public Notification findById(String notificationId) {
        String sql = "SELECT * FROM notifications WHERE notification_id = ?";
        List<Notification> list = jdbcTemplate.query(sql, NOTIFICATION_ROW_MAPPER, notificationId);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public int save(Notification notification) {
        String sql = "INSERT INTO notifications (notification_id, user_id, message, type, is_read) VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                notification.getNotificationId(),
                notification.getUserId(),
                notification.getMessage(),
                notification.getType(),
                notification.isRead()
        );
    }

    @Override
    public int updateIsRead(String notificationId, boolean isRead) {
        String sql = "UPDATE notifications SET is_read = ? WHERE notification_id = ?";
        return jdbcTemplate.update(sql, isRead, notificationId);
    }

    @Override
    public int deleteById(String notificationId) {
        String sql = "DELETE FROM notifications WHERE notification_id = ?";
        return jdbcTemplate.update(sql, notificationId);
    }

    @Override
    public int deleteByUserId(String userId) {
        String sql = "DELETE FROM notifications WHERE user_id = ?";
        return jdbcTemplate.update(sql, userId);
    }
}
