package com.pockettrack.util.mapper;

import com.pockettrack.model.Transaction;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionRowMapper implements RowMapper<Transaction> {
    @Override
    public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
        Transaction txn = new Transaction();
        txn.setTransactionId(rs.getInt("transaction_id"));
        txn.setUserId(rs.getInt("user_id"));
        txn.setCategoryId(rs.getInt("category_id"));
        txn.setType(rs.getString("type"));
        txn.setAmount(rs.getBigDecimal("amount"));
        txn.setTransactionDate(rs.getDate("transaction_date").toLocalDate());
        txn.setDescription(rs.getString("description"));
        txn.setHasBill(rs.getBoolean("has_bill"));
        txn.setBillImagePath(rs.getString("bill_image_path"));
        return txn;
    }
}
