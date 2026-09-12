package com.evms.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for updating an existing DRAFT voucher ({@code PUT /api/vouchers/{id}}).
 * All fields are optional — only provided fields override existing values.
 *
 * @author EVMS Team
 */
@Data
@NoArgsConstructor
public class VoucherUpdateRequest {

    private String department;
    private String voucherDate;
    private String expenseDate;
    private String expenseTitle;
    private String expenseCategory;
    private String expenseDescription;
    private BigDecimal amount;
    private String employeeCode;
}
