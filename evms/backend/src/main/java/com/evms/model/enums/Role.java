package com.evms.model.enums;

/**
 * Enumeration representing the roles available in the EVMS system.
 * <p>
 * Each user is assigned exactly one role that determines their
 * permissions throughout the application:
 * <ul>
 * <li>{@code EMPLOYEE} — Can create, edit, delete, and submit vouchers</li>
 * <li>{@code DIRECTOR} — Can approve or reject submitted vouchers</li>
 * <li>{@code ACCOUNTS} — Can view all vouchers and approved expense ledger</li>
 * </ul>
 *
 * 
 */
public enum Role {

    /** Employee role — creates and submits expense vouchers. */
    EMPLOYEE,

    /** Director role — approves or rejects submitted vouchers. */
    DIRECTOR,

    /** Accounts role — views the expense ledger and approved vouchers. */
    ACCOUNTS
}
