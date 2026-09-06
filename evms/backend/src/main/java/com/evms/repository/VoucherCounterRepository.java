package com.evms.repository;

import com.evms.model.VoucherCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link VoucherCounter} entity.
 * <p>
 * Used to manage the sequential voucher numbering system.
 * Each row tracks the last-used sequence number for a given calendar year.
 *
 * @author EVMS Team
 */
@Repository
public interface VoucherCounterRepository extends JpaRepository<VoucherCounter, Integer> {

    /**
     * Finds the counter row for a specific calendar year.
     *
     * @param year the calendar year (e.g., 2026)
     * @return an Optional containing the counter if it exists for that year
     */
    Optional<VoucherCounter> findByYear(Integer year);
}
