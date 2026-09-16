package com.pockettrack.util.mapper;

import com.pockettrack.model.SavingsLog;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SavingsLogRowMapper implements RowMapper<SavingsLog> {
    @Override
    public SavingsLog mapRow(ResultSet rs, int rowNum) throws SQLException {
        SavingsLog log = new SavingsLog();
        log.setLogId(rs.getInt("log_id"));
        log.setSavingsId(rs.getInt("savings_id"));
        log.setAmount(rs.getBigDecimal("amount"));
        log.setLogType(rs.getString("log_type"));
        log.setLogDate(rs.getTimestamp("log_date").toLocalDateTime());
        log.setNote(rs.getString("note"));
        return log;
    }
}
