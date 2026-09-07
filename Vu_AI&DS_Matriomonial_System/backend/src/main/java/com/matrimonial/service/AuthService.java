package com.matrimonial.service;

import com.matrimonial.dto.AuthResponse;
import com.matrimonial.dto.LoginRequest;
import com.matrimonial.dto.RegisterRequest;
import com.matrimonial.entity.Profile;
import com.matrimonial.entity.User;
import com.matrimonial.exception.DuplicateEmailException;
import com.matrimonial.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(RegisterRequest request, HttpServletRequest httpRequest) {
        if (!request.isPasswordMatching()) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException("Email already registered. Please click 'Log In' below to sign in.");
        }

        User user = new User();
        user.setName(request.getName() != null ? request.getName().trim() : "");
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        // Auto-login after registration
        setAuthenticationInContext(savedUser, httpRequest);

        return new AuthResponse(savedUser.getUserId(), savedUser.getName(), savedUser.getEmail(), false, null);
    }

    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        String rawPassword = request.getPassword();
        String storedPassword = user.getPassword();

        if (storedPassword == null || storedPassword.isBlank()) {
            throw new BadCredentialsException("Invalid email or password");
        }

        if (!passwordEncoder.matches(rawPassword, storedPassword)) {
            throw new BadCredentialsException("Invalid email or password");
        }

        setAuthenticationInContext(user, httpRequest);

        Profile profile = user.getProfile();
        boolean hasProfile = profile != null;
        Long profileId = hasProfile ? profile.getProfileId() : null;

        return new AuthResponse(user.getUserId(), user.getName(), user.getEmail(), hasProfile, profileId);
    }

    public void logout(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
    }

    public AuthResponse getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new BadCredentialsException("User not authenticated");
        }

        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        Profile profile = user.getProfile();
        boolean hasProfile = profile != null;
        Long profileId = hasProfile ? profile.getProfileId() : null;

        return new AuthResponse(user.getUserId(), user.getName(), user.getEmail(), hasProfile, profileId);
    }

    private void setAuthenticationInContext(User user, HttpServletRequest httpRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getEmail(), null, Collections.emptyList());
        
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
    }
}
