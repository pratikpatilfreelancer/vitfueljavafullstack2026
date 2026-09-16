package com.library.userservice.config;

import com.library.userservice.entity.Role;
import com.library.userservice.entity.User;
import com.library.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds a single default LIBRARIAN account on startup, purely for demonstration
 * purposes (the requirements state librarian accounts are created manually - this
 * just automates that "manual" insert so the project is runnable out of the box).
 * Runs only once: if a librarian already exists, it does nothing.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        boolean librarianExists = userRepository.findByEmail("librarian@library.com").isPresent();
        if (librarianExists) {
            return;
        }

        User librarian = new User();
        librarian.setName("Default Librarian");
        librarian.setEmail("librarian@library.com");
        librarian.setPassword(passwordEncoder.encode("librarian123"));
        librarian.setRole(Role.LIBRARIAN);
        userRepository.save(librarian);
    }
}
