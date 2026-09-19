package com.library.userservice.dto;

import com.library.userservice.entity.Role;
import com.library.userservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Safe, outward-facing representation of a User - the password hash is never exposed.
 */
@Getter
@AllArgsConstructor
public class UserResponse {

    private Long userId;
    private String name;
    private String email;
    private Role role;

    public static UserResponse fromEntity(User user) {
        return new UserResponse(user.getUserId(), user.getName(), user.getEmail(), user.getRole());
    }
}
