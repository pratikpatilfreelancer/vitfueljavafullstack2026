package com.evms.repository;

import com.evms.model.Voucher;
import com.evms.model.enums.VoucherStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link Voucher} entity.
 * <p>
 * Extends {@link JpaSpecificationExecutor} to support dynamic filtering
 * (search by voucher number, employee name, department, category, status,
 * date range, and amount range) via JPA Specifications.
 * <p>
 * Custom JPQL queries handle dashboard summary aggregations and
 * role-scoped data access.
 *
 * @author EVMS Team
 */
@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long>,
        JpaSpecificationExecutor<Voucher> {

    /**
     * Finds all vouchers belonging to a specific employee.
     * Used for employee-scoped listing.
     *
     * @param employeeId the employee's user ID
     * @return list of vouchers created by the employee
     */
    List<Voucher> findByEmployeeId(Long employeeId);

    /**
     * Finds all vouchers with a specific status.
     * Used for dashboard summary calculations.
     *
     * @param status the voucher status to filter by
     * @return list of matching vouchers
     */
    List<Voucher> findByStatus(VoucherStatus status);

    /**
     * Finds all vouchers EXCEPT those with a specific status.
     * Used to exclude DRAFTs for non-employee dashboards.
     *
     * @param status the status to exclude
     * @return list of matching vouchers
     */
    List<Voucher> findByStatusNot(VoucherStatus status);

    /**
     * Finds all vouchers belonging to a specific employee with a specific status.
     *
     * @param employeeId the employee's user ID
     * @param status     the voucher status to filter by
     * @return list of matching vouchers
     */
    List<Voucher> findByEmployeeIdAndStatus(Long employeeId, VoucherStatus status);

    /**
     * Counts the number of vouchers with a given status.
     * Used in dashboard summary calculations.
     *
     * @param status the voucher status to count
     * @return the count of matching vouchers
     */
    long countByStatus(VoucherStatus status);

    /**
     * Counts the number of vouchers belonging to a specific employee.
     *
     * @param employeeId the employee's user ID
     * @return total voucher count for the employee
     */
    long countByEmployeeId(Long employeeId);

    /**
     * Finds the most recently updated vouchers (across all employees).
     * Used for the Director's "recent activity" dashboard widget.
     *
     * @param pageable pagination info (typically first 5 results)
     * @return page of recently updated vouchers
     */
    Page<Voucher> findAllByOrderByUpdatedAtDesc(Pageable pageable);

    /**
     * Finds the most recently updated vouchers EXCEPT those with a specific status.
     * Used for Director dashboard to exclude DRAFTs.
     *
     * @param status the status to exclude
     * @param pageable pagination info
     * @return page of recently updated vouchers
     */
    Page<Voucher> findByStatusNotOrderByUpdatedAtDesc(VoucherStatus status, Pageable pageable);

    /**
     * Finds the most recently updated approved vouchers.
     * Used for the Accounts team's dashboard widget.
     *
     * @param status   the status to filter by (APPROVED)
     * @param pageable pagination info (typically first 5 results)
     * @return page of recently approved vouchers
     */
    Page<Voucher> findByStatusOrderByUpdatedAtDesc(VoucherStatus status, Pageable pageable);

    /**
     * Calculates the total amount of all vouchers belonging to an employee.
     * Used for the Employee dashboard's "Total Amount Claimed" KPI.
     *
     * @param employeeId the employee's user ID
     * @return total amount, or null if no vouchers exist
     */
    @Query("SELECT COALESCE(SUM(v.amount), 0) FROM Voucher v WHERE v.employee.id = :employeeId")
    java.math.BigDecimal sumAmountByEmployeeId(@Param("employeeId") Long employeeId);

    /**
     * Calculates the total amount of all vouchers with a given status.
     * Used for dashboard summary calculations (e.g., total pending amount).
     *
     * @param status the voucher status to sum
     * @return total amount for the given status
     */
    @Query("SELECT COALESCE(SUM(v.amount), 0) FROM Voucher v WHERE v.status = :status")
    java.math.BigDecimal sumAmountByStatus(@Param("status") VoucherStatus status);

    /**
     * Counts vouchers with a given status that were updated today.
     * Used for the Director's "approved today" / "rejected today" KPIs.
     *
     * @param status    the status to filter by
     * @param startOfDay start of the current day
     * @param endOfDay   end of the current day
     * @return count of matching vouchers
     */
    @Query("SELECT COUNT(v) FROM Voucher v WHERE v.status = :status " +
            "AND v.updatedAt >= :startOfDay AND v.updatedAt <= :endOfDay")
    long countByStatusAndUpdatedAtBetween(
            @Param("status") VoucherStatus status,
            @Param("startOfDay") java.time.LocalDateTime startOfDay,
            @Param("endOfDay") java.time.LocalDateTime endOfDay);
}
