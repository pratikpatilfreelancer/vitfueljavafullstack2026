package com.evms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA Entity representing the voucher number counter.
 * <p>
 * Maps to the {@code voucher_counter} table in MySQL. Stores one row per
 * calendar year, tracking the running sequence number for voucher numbering.
 *
 * @author EVMS Team
 */
@Entity
@Table(name = "voucher_counter")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoucherCounter {

    /** Calendar year (e.g., 2026). Serves as the primary key. */
    @Id
    @Column(name = "year")
    private Integer year;

    /** Running sequence number for the given year. */
    @Column(name = "seq", nullable = false)
    private Integer seq;
}
