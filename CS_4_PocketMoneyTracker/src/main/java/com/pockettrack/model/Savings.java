package com.pockettrack.model;

import java.math.BigDecimal;

public class Savings {
    private int savingsId;
    private int userId;
    private BigDecimal balance;

    public int getSavingsId() { return savingsId; }
    public void setSavingsId(int savingsId) { this.savingsId = savingsId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}
