package com.evms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard error response DTO for all API errors.
 * JSON shape: {@code { "error": "message" }}
 *
 * @author EVMS Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private String error;
}
