package com.internships.service;

import com.internships.dao.ApplicationDao;
import com.internships.dao.InternshipDao;
import com.internships.dao.StudentDao;
import com.internships.dao.UserDao;
import com.internships.exception.NotFoundException;
import com.internships.model.InternshipApplication;
import com.internships.model.Notification;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationDao applicationDao;
    private final StudentDao studentDao;
    private final InternshipDao internshipDao;
    private final NotificationService notificationService;
    private final UserDao userDao;

    private static final List<String> ALLOWED_STATUSES = Arrays.asList("Applied", "Shortlisted", "Interview", "Selected", "Rejected");

    public ApplicationServiceImpl(ApplicationDao applicationDao, StudentDao studentDao, InternshipDao internshipDao, NotificationService notificationService, UserDao userDao) {
        this.applicationDao = applicationDao;
        this.studentDao = studentDao;
        this.internshipDao = internshipDao;
        this.notificationService = notificationService;
        this.userDao = userDao;
    }

    @Override
    public List<InternshipApplication> getAllApplications() {
        return applicationDao.findAll();
    }

    @Override
    public InternshipApplication getApplicationById(String applicationId) {
        InternshipApplication app = applicationDao.findById(applicationId);
        if (app == null) {
            throw new NotFoundException("Application not found with id: " + applicationId);
        }
        return app;
    }

    @Override
    public List<InternshipApplication> getApplicationsByStudentId(String studentId) {
        return applicationDao.findByStudentId(studentId);
    }

    @Override
    public List<InternshipApplication> getApplicationsByInternshipId(String internshipId) {
        return applicationDao.findByInternshipId(internshipId);
    }

    @Override
    public List<InternshipApplication> getApplicationsByStatus(String status) {
        if (!ALLOWED_STATUSES.contains(status)) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        return applicationDao.findByStatus(status);
    }

    @Override
    public InternshipApplication createApplication(InternshipApplication application) {
        if (application.getApplicationId() == null || application.getApplicationId().isBlank()) {
            throw new IllegalArgumentException("Application ID is required");
        }
        if (application.getStudentId() == null || application.getStudentId().isBlank()) {
            throw new IllegalArgumentException("Student ID is required");
        }
        if (application.getInternshipId() == null || application.getInternshipId().isBlank()) {
            throw new IllegalArgumentException("Internship ID is required");
        }
        if (studentDao.findById(application.getStudentId()) == null) {
            throw new NotFoundException("Student not found with id: " + application.getStudentId());
        }
        if (internshipDao.findById(application.getInternshipId()) == null) {
            throw new NotFoundException("Internship not found with id: " + application.getInternshipId());
        }
        if (application.getStatus() == null || application.getStatus().isBlank()) {
            application.setStatus("Applied");
        } else if (!ALLOWED_STATUSES.contains(application.getStatus())) {
            throw new IllegalArgumentException("Invalid status: " + application.getStatus());
        }
        if (applicationDao.findById(application.getApplicationId()) != null) {
            throw new IllegalArgumentException("Application with id already exists: " + application.getApplicationId());
        }
        applicationDao.save(application);
        return applicationDao.findById(application.getApplicationId());
    }

    @Override
    public InternshipApplication updateApplication(String applicationId, InternshipApplication application) {
        InternshipApplication existing = applicationDao.findById(applicationId);
        if (existing == null) {
            throw new NotFoundException("Application not found with id: " + applicationId);
        }
        if (application.getStudentId() == null || application.getStudentId().isBlank()) {
            throw new IllegalArgumentException("Student ID is required");
        }
        if (application.getInternshipId() == null || application.getInternshipId().isBlank()) {
            throw new IllegalArgumentException("Internship ID is required");
        }
        if (studentDao.findById(application.getStudentId()) == null) {
            throw new NotFoundException("Student not found with id: " + application.getStudentId());
        }
        if (internshipDao.findById(application.getInternshipId()) == null) {
            throw new NotFoundException("Internship not found with id: " + application.getInternshipId());
        }
        if (application.getStatus() != null && !application.getStatus().isBlank() && !ALLOWED_STATUSES.contains(application.getStatus())) {
            throw new IllegalArgumentException("Invalid status: " + application.getStatus());
        }
        application.setApplicationId(applicationId);
        applicationDao.update(application);
        return applicationDao.findById(applicationId);
    }

    @Override
    public InternshipApplication updateApplicationStatus(String applicationId, String status) {
        InternshipApplication existing = applicationDao.findById(applicationId);
        if (existing == null) {
            throw new NotFoundException("Application not found with id: " + applicationId);
        }
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status is required");
        }
        if (!ALLOWED_STATUSES.contains(status)) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        String oldStatus = existing.getStatus();
        applicationDao.updateStatus(applicationId, status);
        InternshipApplication updated = applicationDao.findById(applicationId);

        if (!status.equals(oldStatus)) {
            String message = "Your application status has been updated from " + oldStatus + " to " + status;
            createNotificationForStudent(existing.getStudentId(), message, "STATUS_UPDATE");
        }

        return updated;
    }

    private void createNotificationForStudent(String studentId, String message, String type) {
        try {
            com.internships.model.User user = userDao.findByEmail(studentId + "@example.com");
            if (user == null) {
                user = userDao.findById(studentId);
            }
            if (user != null) {
                Notification notification = new Notification();
                notification.setNotificationId("notif-" + UUID.randomUUID().toString());
                notification.setUserId(user.getUserId());
                notification.setMessage(message);
                notification.setType(type);
                notification.setRead(false);
                notificationService.createNotification(notification);
            }
        } catch (Exception e) {
            System.err.println("Failed to create notification: " + e.getMessage());
        }
    }

    @Override
    public void deleteApplication(String applicationId) {
        InternshipApplication existing = applicationDao.findById(applicationId);
        if (existing == null) {
            throw new NotFoundException("Application not found with id: " + applicationId);
        }
        applicationDao.deleteById(applicationId);
    }

    @Override
    public long countByStatus(String status) {
        return applicationDao.countByStatus(status);
    }

    @Override
    public long countAll() {
        return applicationDao.countAll();
    }
}
