package com.pockettrack.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {

    private int transactionId;
    private int userId;
    private Integer categoryId;
    private String type;
    private BigDecimal amount;
    private LocalDate transactionDate;
    private String description;
    private boolean hasBill;
    private String billImagePath;

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isHasBill() {
        return hasBill;
    }

    public void setHasBill(boolean hasBill) {
        this.hasBill = hasBill;
    }

    public String getBillImagePath() {
        return billImagePath;
    }

    public void setBillImagePath(String billImagePath) {
        this.billImagePath = billImagePath;
    }
}