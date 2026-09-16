package com.pockettrack.dao.impl;

import com.pockettrack.dao.UserDao;
import com.pockettrack.model.User;
import com.pockettrack.util.mapper.UserRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {
    private final JdbcTemplate jdbcTemplate;

    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User save(User user) {
        String sql = "INSERT INTO `User` (name, email, password) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            return ps;
        }, keyHolder);
        user.setUserId(keyHolder.getKey().intValue());
        return findById(user.getUserId()).orElse(user);
    }

    @Override
    public Optional<User> findById(int userId) {
        String sql = "SELECT * FROM `User` WHERE user_id = ?";
        return jdbcTemplate.query(sql, new UserRowMapper(), userId).stream().findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM `User` WHERE email = ?";
        return jdbcTemplate.query(sql, new UserRowMapper(), email).stream().findFirst();
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM `User` ORDER BY user_id", new UserRowMapper());
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE `User` SET name = ?, email = ?, password = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, user.getName(), user.getEmail(), user.getPassword(), user.getUserId());
    }

    @Override
    public void deleteById(int userId) {
        jdbcTemplate.update("DELETE FROM `User` WHERE user_id = ?", userId);
    }
}
