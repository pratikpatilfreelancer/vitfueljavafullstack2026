package com.pockettrack.dao.impl;

import com.pockettrack.dao.TransactionDao;
import com.pockettrack.model.Transaction;
import com.pockettrack.util.mapper.TransactionRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

@Repository
public class TransactionDaoImpl implements TransactionDao {
    private final JdbcTemplate jdbcTemplate;

    public TransactionDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Transaction save(Transaction txn) {
        String sql = "INSERT INTO `Transaction` " +
                "(user_id, category_id, type, amount, transaction_date, description, has_bill, bill_image_path) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, txn.getUserId());
            ps.setInt(2, txn.getCategoryId());
            ps.setString(3, txn.getType());
            ps.setBigDecimal(4, txn.getAmount());
            ps.setObject(5, txn.getTransactionDate());
            ps.setString(6, txn.getDescription());
            ps.setBoolean(7, txn.isHasBill());
            ps.setString(8, txn.getBillImagePath());
            return ps;
        }, keyHolder);
        txn.setTransactionId(keyHolder.getKey().intValue());
        return findById(txn.getTransactionId()).orElse(txn);
    }

    @Override
    public Optional<Transaction> findById(int transactionId) {
        return jdbcTemplate.query(
            "SELECT * FROM `Transaction` WHERE transaction_id = ?",
            new TransactionRowMapper(), transactionId
        ).stream().findFirst();
    }

    @Override
    public List<Transaction> findByUserAndMonth(int userId, int month, int year) {
        String sql = "SELECT * FROM `Transaction` " +
                "WHERE user_id = ? AND MONTH(transaction_date) = ? AND YEAR(transaction_date) = ? " +
                "ORDER BY transaction_date DESC, transaction_id DESC";
        return jdbcTemplate.query(sql, new TransactionRowMapper(), userId, month, year);
    }

    @Override
    public BigDecimal sumByUserMonthAndType(int userId, int month, int year, String type) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM `Transaction` " +
                "WHERE user_id = ? AND MONTH(transaction_date) = ? AND YEAR(transaction_date) = ? AND type = ?";
        BigDecimal result = jdbcTemplate.queryForObject(sql, BigDecimal.class, userId, month, year, type);
        return result == null ? BigDecimal.ZERO : result;
    }

    @Override
    public List<Transaction> findDailyBreakdown(int userId, int month, int year) {
        String sql = "SELECT * FROM `Transaction` " +
                "WHERE user_id = ? AND MONTH(transaction_date) = ? AND YEAR(transaction_date) = ? " +
                "ORDER BY transaction_date ASC, transaction_id ASC";
        return jdbcTemplate.query(sql, new TransactionRowMapper(), userId, month, year);
    }

    @Override
    public void deleteById(int transactionId) {
        jdbcTemplate.update("DELETE FROM `Transaction` WHERE transaction_id = ?", transactionId);
    }
}
