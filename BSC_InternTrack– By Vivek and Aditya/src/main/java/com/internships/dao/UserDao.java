package com.internships.dao;

import com.internships.model.User;
import java.util.List;

public interface UserDao {
    User findByEmail(String email);
    User findById(String userId);
    List<User> findAll();
    int save(User user);
    int update(User user);
    int deleteById(String userId);
}
