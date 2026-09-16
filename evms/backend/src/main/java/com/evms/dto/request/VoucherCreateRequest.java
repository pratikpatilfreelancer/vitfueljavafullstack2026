package com.evms.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for creating a new expense voucher ({@code POST /api/vouchers}).
 *
 * @author EVMS Team
 */
@Data
@NoArgsConstructor
public class VoucherCreateRequest {

    private String department;
    private String voucherDate;
    private String expenseDate;
    private String expenseTitle;
    private String expenseCategory;
    private String expenseDescription;
    private BigDecimal amount;
    private String employeeCode;
}
