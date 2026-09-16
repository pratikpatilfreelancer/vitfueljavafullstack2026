package com.pockettrack.util.mapper;

import com.pockettrack.model.Savings;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SavingsRowMapper implements RowMapper<Savings> {
    @Override
    public Savings mapRow(ResultSet rs, int rowNum) throws SQLException {
        Savings savings = new Savings();
        savings.setSavingsId(rs.getInt("savings_id"));
        savings.setUserId(rs.getInt("user_id"));
        savings.setBalance(rs.getBigDecimal("balance"));
        return savings;
    }
}
