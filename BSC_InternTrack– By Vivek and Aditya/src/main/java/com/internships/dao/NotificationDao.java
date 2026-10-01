package com.internships.dao;

import com.internships.model.Notification;
import java.util.List;

public interface NotificationDao {
    List<Notification> findByUserId(String userId);
    List<Notification> findByUserIdAndIsRead(String userId, boolean isRead);
    Notification findById(String notificationId);
    int save(Notification notification);
    int updateIsRead(String notificationId, boolean isRead);
    int deleteById(String notificationId);
    int deleteByUserId(String userId);
}
