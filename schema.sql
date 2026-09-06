-- ============================================
-- Attendance Management System — Database Schema
-- Matches ATTENDANCE_SYSTEM_SPEC.md Section 6
-- ============================================

CREATE DATABASE IF NOT EXISTS ams_db;
USE ams_db;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,          -- ADMIN / TEACHER
    email VARCHAR(100)
);

CREATE TABLE batch (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    batch_code VARCHAR(20) NOT NULL UNIQUE,
    subject VARCHAR(100) NOT NULL,
    teacher_id BIGINT,
    batch_days VARCHAR(20),
    batch_timing VARCHAR(20),
    FOREIGN KEY (teacher_id) REFERENCES users(id)
);

CREATE TABLE student (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    enrollment_no VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    batch_id BIGINT,
    email VARCHAR(100),
    phone VARCHAR(15),
    FOREIGN KEY (batch_id) REFERENCES batch(id)
);

CREATE TABLE attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    batch_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    marked_time TIME NOT NULL,
    status VARCHAR(10) NOT NULL,        -- PRESENT / ABSENT
    marked_by BIGINT,
    FOREIGN KEY (student_id) REFERENCES student(id),
    FOREIGN KEY (batch_id) REFERENCES batch(id),
    FOREIGN KEY (marked_by) REFERENCES users(id),
    UNIQUE KEY uniq_attendance (student_id, attendance_date)
);
