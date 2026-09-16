package com.pockettrack.dao.impl;

import com.pockettrack.dao.SavingsDao;
import com.pockettrack.model.Savings;
import com.pockettrack.util.mapper.SavingsRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import java.math.BigDecimal;

@Repository
public class SavingsDaoImpl implements SavingsDao {
    private final JdbcTemplate jdbcTemplate;

    public SavingsDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Savings save(Savings savings) {
        String sql = "INSERT INTO Savings (user_id, balance) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, savings.getUserId());
            ps.setBigDecimal(2, savings.getBalance());
            return ps;
        }, keyHolder);
        savings.setSavingsId(keyHolder.getKey().intValue());
        return savings;
    }

    @Override
    public Optional<Savings> findByUserId(int userId) {
        return jdbcTemplate.query(
            "SELECT * FROM Savings WHERE user_id = ?",
            new SavingsRowMapper(), userId
        ).stream().findFirst();
    }

    @Override
    public void updateBalance(int userId, BigDecimal newBalance) {
        jdbcTemplate.update("UPDATE Savings SET balance = ? WHERE user_id = ?", newBalance, userId);
    }
}
