-- ===========================================================================
-- EVMS Seed Data — Inserted on application startup via Spring Boot data.sql
-- ===========================================================================
-- Password for ALL demo accounts: password123
-- BCrypt hash (cost factor 10): $2a$10$dTuP/j3.JiFQVE5pTfk8vuyCzQ4ke8Ub/vVYeXOk5Ed5d1mKFvJk6
-- ===========================================================================

-- ---- Core demo accounts (3 roles) ----
INSERT IGNORE INTO users (id, name, email, password_hash, role, department, created_at) VALUES
(1, 'John Employee', 'employee@evms.test', '$2a$10$dTuP/j3.JiFQVE5pTfk8vuyCzQ4ke8Ub/vVYeXOk5Ed5d1mKFvJk6', 'EMPLOYEE', 'Engineering', NOW()),
(2, 'Jane Director', 'director@evms.test', '$2a$10$dTuP/j3.JiFQVE5pTfk8vuyCzQ4ke8Ub/vVYeXOk5Ed5d1mKFvJk6', 'DIRECTOR', 'Engineering', NOW()),
(3, 'Finance Admin', 'accounts@evms.test', '$2a$10$dTuP/j3.JiFQVE5pTfk8vuyCzQ4ke8Ub/vVYeXOk5Ed5d1mKFvJk6', 'ACCOUNTS', 'Finance', NOW()),
(4, 'Rahul Sharma', 'rahul.sharma@evms.test', '$2a$10$dTuP/j3.JiFQVE5pTfk8vuyCzQ4ke8Ub/vVYeXOk5Ed5d1mKFvJk6', 'EMPLOYEE', 'Sales', NOW()),
(5, 'Priya Patel', 'priya.patel@evms.test', '$2a$10$dTuP/j3.JiFQVE5pTfk8vuyCzQ4ke8Ub/vVYeXOk5Ed5d1mKFvJk6', 'EMPLOYEE', 'Marketing', NOW()),
(6, 'Amit Kumar', 'amit.kumar@evms.test', '$2a$10$dTuP/j3.JiFQVE5pTfk8vuyCzQ4ke8Ub/vVYeXOk5Ed5d1mKFvJk6', 'EMPLOYEE', 'IT', NOW()),
(7, 'Sneha Reddy', 'sneha.reddy@evms.test', '$2a$10$dTuP/j3.JiFQVE5pTfk8vuyCzQ4ke8Ub/vVYeXOk5Ed5d1mKFvJk6', 'EMPLOYEE', 'HR', NOW()),
(8, 'Karthik Nair', 'karthik.nair@evms.test', '$2a$10$dTuP/j3.JiFQVE5pTfk8vuyCzQ4ke8Ub/vVYeXOk5Ed5d1mKFvJk6', 'EMPLOYEE', 'Operations', NOW()),
(9, 'Divya Joshi', 'divya.joshi@evms.test', '$2a$10$dTuP/j3.JiFQVE5pTfk8vuyCzQ4ke8Ub/vVYeXOk5Ed5d1mKFvJk6', 'EMPLOYEE', 'Finance', NOW()),
(10, 'Rohan Mehta', 'rohan.mehta@evms.test', '$2a$10$dTuP/j3.JiFQVE5pTfk8vuyCzQ4ke8Ub/vVYeXOk5Ed5d1mKFvJk6', 'EMPLOYEE', 'Engineering', NOW()),
(11, 'Ananya Gupta', 'ananya.gupta@evms.test', '$2a$10$dTuP/j3.JiFQVE5pTfk8vuyCzQ4ke8Ub/vVYeXOk5Ed5d1mKFvJk6', 'EMPLOYEE', 'Sales', NOW()),
(12, 'Suresh Iyer', 'suresh.iyer@evms.test', '$2a$10$dTuP/j3.JiFQVE5pTfk8vuyCzQ4ke8Ub/vVYeXOk5Ed5d1mKFvJk6', 'EMPLOYEE', 'IT', NOW()),
(13, 'Meera Desai', 'meera.desai@evms.test', '$2a$10$dTuP/j3.JiFQVE5pTfk8vuyCzQ4ke8Ub/vVYeXOk5Ed5d1mKFvJk6', 'EMPLOYEE', 'HR', NOW());
