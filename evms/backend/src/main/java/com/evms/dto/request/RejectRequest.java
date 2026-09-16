package com.evms.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for rejecting a submitted voucher ({@code POST /api/vouchers/{id}/reject}).
 *
 * @author EVMS Team
 */
@Data
@NoArgsConstructor
public class RejectRequest {

    /** The reason for rejection (mandatory). */
    private String rejectionReason;
}
