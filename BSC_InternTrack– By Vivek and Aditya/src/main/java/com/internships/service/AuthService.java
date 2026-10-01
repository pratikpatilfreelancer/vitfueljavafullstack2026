package com.internships.service;

import com.internships.dao.UserDao;
import com.internships.exception.DuplicateResourceException;
import com.internships.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final UserDao userDao;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User login(String email, String password) {
        User user = userDao.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        return user;
    }

    public User register(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (user.getRole() == null || user.getRole().isBlank()) {
            throw new IllegalArgumentException("Role is required");
        }
        if (userDao.findByEmail(user.getEmail()) != null) {
            throw new DuplicateResourceException("User already exists with email: " + user.getEmail());
        }
        user.setUserId("user-" + UUID.randomUUID().toString().substring(0, 8));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.save(user);
        return userDao.findByEmail(user.getEmail());
    }
}
