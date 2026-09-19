package com.library.libraryservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Mirrors the shape of the User Service's UserResponse. Kept as a small,
 * independent copy (rather than a shared library) so the two services remain
 * deployable and versionable independently - a deliberate microservices trade-off.
 */
@Getter
@Setter
@NoArgsConstructor
public class UserInfoResponse {
    private Long userId;
    private String name;
    private String email;
    private String role;
}
