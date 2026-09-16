package com.ams.service.impl;

import com.ams.dto.TeacherDto;
import com.ams.entity.User;
import com.ams.exception.DuplicateRecordException;
import com.ams.exception.ResourceNotFoundException;
import com.ams.repository.UserRepository;
import com.ams.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> findAllTeachers() {
        return userRepository.findByRole(User.Role.TEACHER);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    @Override
    public User save(TeacherDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateRecordException("Username already exists: " + dto.getUsername());
        }
        User user = new User();
        user.setFullName(dto.getFullName());
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(User.Role.TEACHER);
        user.setEmail(dto.getEmail());
        return userRepository.save(user);
    }

    @Override
    public User update(Long id, TeacherDto dto) {
        User user = findById(id);
        if (!user.getUsername().equals(dto.getUsername()) && userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateRecordException("Username already exists: " + dto.getUsername());
        }
        user.setFullName(dto.getFullName());
        user.setUsername(dto.getUsername());
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        user.setEmail(dto.getEmail());
        return userRepository.save(user);
    }

    @Override
    public void delete(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    @Override
    public long countTeachers() {
        return userRepository.findByRole(User.Role.TEACHER).size();
    }

    @Override
    public void changePassword(Long userId, String newPassword) {
        User user = findById(userId);
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
    }
}
