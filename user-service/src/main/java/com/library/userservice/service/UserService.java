package com.library.userservice.service;

import com.library.userservice.dto.RegisterRequest;
import com.library.userservice.dto.UpdateUserRequest;
import com.library.userservice.dto.UserResponse;
import com.library.userservice.entity.Role;
import com.library.userservice.entity.User;
import com.library.userservice.exception.DuplicateEmailException;
import com.library.userservice.exception.InvalidCredentialsException;
import com.library.userservice.exception.UserNotFoundException;
import com.library.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for user registration, authentication and profile management.
 * Controllers stay thin; all rules (uniqueness, password hashing, ownership) live here.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("An account with this email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // Public registration always creates a STUDENT account.
        // LIBRARIAN accounts are inserted directly into the database for demonstration.
        user.setRole(Role.STUDENT);

        User saved = userRepository.save(user);
        return UserResponse.fromEntity(saved);
    }

    /**
     * Verifies email/password credentials and returns the matching user.
     * Used directly by the frontend, and also called internally (over REST)
     * by the Library Service, which has no user table of its own.
     */
    public UserResponse login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return UserResponse.fromEntity(user);
    }

    /** A user may fetch their own profile; a LIBRARIAN may fetch anyone's. */
    public UserResponse getUserById(Long userId, Authentication requester) {
        User user = findUserOrThrow(userId);
        assertOwnerOrLibrarian(user, requester);
        return UserResponse.fromEntity(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    @Transactional
    public UserResponse updateUser(Long userId, UpdateUserRequest request, Authentication requester) {
        User user = findUserOrThrow(userId);
        assertOwnerOrLibrarian(user, requester);

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return UserResponse.fromEntity(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = findUserOrThrow(userId);
        userRepository.delete(user);
    }

    private void assertOwnerOrLibrarian(User targetUser, Authentication requester) {
        boolean isLibrarian = requester.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_LIBRARIAN"));
        boolean isOwner = targetUser.getEmail().equals(requester.getName());

        if (!isLibrarian && !isOwner) {
            throw new AccessDeniedException("You can only access your own profile");
        }
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }
}
