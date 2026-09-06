package com.evms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for the login endpoint.
 * Returns a JWT token and the authenticated user's profile.
 *
 * @author EVMS Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private UserPayload user;

    /**
     * Nested object representing the user profile in login responses.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserPayload {

        private Long id;
        private String name;
        private String email;
        private String role;
        private String department;
    }
}
