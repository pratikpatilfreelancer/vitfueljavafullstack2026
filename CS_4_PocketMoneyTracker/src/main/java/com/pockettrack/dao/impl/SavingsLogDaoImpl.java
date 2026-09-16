package com.pockettrack.dao.impl;

import com.pockettrack.dao.SavingsLogDao;
import com.pockettrack.model.SavingsLog;
import com.pockettrack.util.mapper.SavingsLogRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class SavingsLogDaoImpl implements SavingsLogDao {
    private final JdbcTemplate jdbcTemplate;

    public SavingsLogDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public SavingsLog save(SavingsLog log) {
        String sql = "INSERT INTO SavingsLog (savings_id, amount, log_type, note) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, log.getSavingsId());
            ps.setBigDecimal(2, log.getAmount());
            ps.setString(3, log.getLogType());
            ps.setString(4, log.getNote());
            return ps;
        }, keyHolder);
        log.setLogId(keyHolder.getKey().intValue());
        return jdbcTemplate.query(
            "SELECT * FROM SavingsLog WHERE log_id = ?",
            new SavingsLogRowMapper(), log.getLogId()
        ).stream().findFirst().orElse(log);
    }

    @Override
    public List<SavingsLog> findBySavingsId(int savingsId) {
        return jdbcTemplate.query(
            "SELECT * FROM SavingsLog WHERE savings_id = ? ORDER BY log_date DESC, log_id DESC",
            new SavingsLogRowMapper(), savingsId
        );
    }
}
