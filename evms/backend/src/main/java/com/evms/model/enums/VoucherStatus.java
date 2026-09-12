package com.evms.model.enums;

/**
 * Enumeration representing the lifecycle status of an expense voucher.
 * <p>
 * Voucher status transitions follow a strict workflow:
 * <pre>
 *   DRAFT ──(submit)──▶ SUBMITTED ──(approve)──▶ APPROVED  (terminal)
 *                                  ──(reject)───▶ REJECTED  (terminal)
 * </pre>
 * <ul>
 *   <li>{@code DRAFT} — Newly created; editable and deletable by the owner</li>
 *   <li>{@code SUBMITTED} — Sent for director approval; read-only to employee</li>
 *   <li>{@code APPROVED} — Approved by a director with signature; terminal state</li>
 *   <li>{@code REJECTED} — Rejected by a director with reason; terminal state</li>
 * </ul>
 *
 * @author EVMS Team
 */
public enum VoucherStatus {

    /** Initial state — voucher can be edited or deleted by the employee. */
    DRAFT,

    /** Awaiting director approval — read-only to the employee. */
    SUBMITTED,

    /** Approved by a director — terminal, read-only state. */
    APPROVED,

    /** Rejected by a director — terminal, read-only state. */
    REJECTED
}
