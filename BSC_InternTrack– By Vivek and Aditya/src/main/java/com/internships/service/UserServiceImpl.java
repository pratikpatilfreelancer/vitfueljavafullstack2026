package com.internships.service;

import com.internships.dao.UserDao;
import com.internships.exception.DuplicateResourceException;
import com.internships.exception.NotFoundException;
import com.internships.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public User findById(String userId) {
        User user = userDao.findById(userId);
        if (user == null) {
            throw new NotFoundException("User not found with id: " + userId);
        }
        return user;
    }

    @Override
    public User createUser(User user) {
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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.save(user);
        return userDao.findByEmail(user.getEmail());
    }

    @Override
    public User updateUser(User user) {
        User existing = userDao.findById(user.getUserId());
        if (existing == null) {
            throw new NotFoundException("User not found with id: " + user.getUserId());
        }
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(existing.getPassword());
        }
        userDao.update(user);
        return userDao.findById(user.getUserId());
    }

    @Override
    public void deleteUser(String userId) {
        User existing = userDao.findById(userId);
        if (existing == null) {
            throw new NotFoundException("User not found with id: " + userId);
        }
        userDao.deleteById(userId);
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }
}
