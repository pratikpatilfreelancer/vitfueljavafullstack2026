package com.pockettrack.dao;

import com.pockettrack.model.User;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    User save(User user);
    Optional<User> findById(int userId);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    void update(User user);
    void deleteById(int userId);
}
