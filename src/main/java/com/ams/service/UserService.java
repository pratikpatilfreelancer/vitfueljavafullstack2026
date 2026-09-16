package com.ams.service;

import com.ams.dto.TeacherDto;
import com.ams.entity.User;
import java.util.List;

public interface UserService {
    List<User> findAllTeachers();
    User findById(Long id);
    User findByUsername(String username);
    User save(TeacherDto dto);
    User update(Long id, TeacherDto dto);
    void delete(Long id);
    long countTeachers();
    void changePassword(Long userId, String newPassword);
}
