package com.internships.service;

import com.internships.model.User;
import java.util.List;

public interface UserService {
    User findByEmail(String email);
    User findById(String userId);
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(String userId);
    List<User> findAll();
}
