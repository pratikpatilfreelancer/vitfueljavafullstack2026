package com.example.leavemanagement.entity;

/**
 * Represents the possible states of a leave request.
 * Using an enum instead of plain Strings/ints avoids typos like
 * "Aproved" or magic numbers like status = 1, and gives compile-time safety.
 */
public enum LeaveStatus {
    PENDING,
    APPROVED,
    REJECTED
}
