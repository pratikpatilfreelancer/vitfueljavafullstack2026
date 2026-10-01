package com.internships.dao;

import com.internships.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<User> USER_ROW_MAPPER = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User u = new User();
            u.setUserId(rs.getString("user_id"));
            u.setEmail(rs.getString("email"));
            u.setPassword(rs.getString("password"));
            u.setRole(rs.getString("role"));
            u.setRefId(rs.getString("ref_id"));
            u.setCreatedAt(rs.getTimestamp("created_at").toString());
            return u;
        }
    };

    @Override
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        List<User> list = jdbcTemplate.query(sql, USER_ROW_MAPPER, email);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public User findById(String userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        List<User> list = jdbcTemplate.query(sql, USER_ROW_MAPPER, userId);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public int save(User user) {
        String sql = "INSERT INTO users (user_id, email, password, role, ref_id) VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                user.getUserId(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.getRefId()
        );
    }

    @Override
    public int update(User user) {
        String sql = "UPDATE users SET email = ?, password = ?, role = ?, ref_id = ? WHERE user_id = ?";
        return jdbcTemplate.update(sql,
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.getRefId(),
                user.getUserId()
        );
    }

    @Override
    public int deleteById(String userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        return jdbcTemplate.update(sql, userId);
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, USER_ROW_MAPPER);
    }
}
