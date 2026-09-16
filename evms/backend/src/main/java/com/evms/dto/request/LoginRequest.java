package com.evms.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for the login endpoint ({@code POST /api/auth/login}).
 *
 * @author EVMS Team
 */
@Data
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Email and password are required.")
    @Email(message = "Invalid email format.")
    private String email;

    @NotBlank(message = "Email and password are required.")
    private String password;
}
