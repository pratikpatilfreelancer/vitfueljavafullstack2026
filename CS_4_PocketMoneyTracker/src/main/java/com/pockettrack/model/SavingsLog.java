package com.pockettrack.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SavingsLog {
    private int logId;
    private int savingsId;
    private BigDecimal amount;
    private String logType;
    private LocalDateTime logDate;
    private String note;

    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }
    public int getSavingsId() { return savingsId; }
    public void setSavingsId(int savingsId) { this.savingsId = savingsId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getLogType() { return logType; }
    public void setLogType(String logType) { this.logType = logType; }
    public LocalDateTime getLogDate() { return logDate; }
    public void setLogDate(LocalDateTime logDate) { this.logDate = logDate; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
