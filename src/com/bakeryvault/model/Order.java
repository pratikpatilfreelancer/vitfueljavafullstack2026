package com.bakeryvault.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single purchase, refund, or freshness-check event. Used
 * for the order log (file persistence) and for reporting via streams.
 */
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public enum Type { PURCHASE, REFUND, FRESHNESS_CHECK }

    private final String itemId;
    private final String customerId;
    private final Type type;
    private final LocalDateTime timestamp;
    private final LocalDate expirationDate;

    public Order(String itemId, String customerId, Type type, LocalDate expirationDate) {
        this.itemId = itemId;
        this.customerId = customerId;
        this.type = type;
        this.timestamp = LocalDateTime.now();
        this.expirationDate = expirationDate;
    }

    public String getItemId() {
        return itemId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public Type getType() {
        return type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public boolean isExpired() {
        return type == Type.PURCHASE && expirationDate != null && LocalDate.now().isAfter(expirationDate);
    }

    /** Line format used when writing to the plain-text order log file. */
    public String toLogLine() {
        return String.format("%s | %s | item=%s | customer=%s | expires=%s",
                timestamp.format(FORMATTER), type, itemId, customerId,
                expirationDate == null ? "-" : expirationDate);
    }

    @Override
    public String toString() {
        return toLogLine();
    }
}
