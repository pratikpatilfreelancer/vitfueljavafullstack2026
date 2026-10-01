package com.internships.service;

import com.internships.dao.NotificationDao;
import com.internships.exception.NotFoundException;
import com.internships.model.Notification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationDao notificationDao;

    public NotificationService(NotificationDao notificationDao) {
        this.notificationDao = notificationDao;
    }

    public List<Notification> getNotificationsByUserId(String userId) {
        return notificationDao.findByUserId(userId);
    }

    public List<Notification> getUnreadNotificationsByUserId(String userId) {
        return notificationDao.findByUserIdAndIsRead(userId, false);
    }

    public Notification getNotificationById(String notificationId) {
        Notification notification = notificationDao.findById(notificationId);
        if (notification == null) {
            throw new NotFoundException("Notification not found with id: " + notificationId);
        }
        return notification;
    }

    public Notification createNotification(Notification notification) {
        if (notification.getUserId() == null || notification.getUserId().isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (notification.getMessage() == null || notification.getMessage().isBlank()) {
            throw new IllegalArgumentException("Message is required");
        }
        notificationDao.save(notification);
        return notification;
    }

    public Notification markAsRead(String notificationId) {
        Notification notification = notificationDao.findById(notificationId);
        if (notification == null) {
            throw new NotFoundException("Notification not found with id: " + notificationId);
        }
        notificationDao.updateIsRead(notificationId, true);
        return notificationDao.findById(notificationId);
    }

    public void deleteNotification(String notificationId) {
        Notification notification = notificationDao.findById(notificationId);
        if (notification == null) {
            throw new NotFoundException("Notification not found with id: " + notificationId);
        }
        notificationDao.deleteById(notificationId);
    }

    public void deleteAllNotificationsByUserId(String userId) {
        notificationDao.deleteByUserId(userId);
    }
}
