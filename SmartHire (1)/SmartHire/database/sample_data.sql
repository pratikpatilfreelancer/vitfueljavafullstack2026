-- ============================================================
-- SmartHire - Sample data for demo/testing purposes
-- Run schema.sql first, then this file.
-- All demo accounts use the password: password123
-- ============================================================

USE smarthire;

-- ------------------------------------------------------------
-- Users (2 recruiters, 4 candidates)
-- ------------------------------------------------------------
INSERT INTO users (username, password, role, email, full_name) VALUES
('recruiter1', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'RECRUITER', 'recruiter1@techcorp.com', 'Anita Sharma'),
('recruiter2', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'RECRUITER', 'recruiter2@innovate.com', 'Rahul Mehta'),
('candidate1', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'CANDIDATE', 'candidate1@mail.com', 'Priya Nair'),
('candidate2', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'CANDIDATE', 'candidate2@mail.com', 'Aditya Rao'),
('candidate3', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'CANDIDATE', 'candidate3@mail.com', 'Sneha Kulkarni'),
('candidate4', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'CANDIDATE', 'candidate4@mail.com', 'Vikram Singh');

-- ------------------------------------------------------------
-- Recruiter profiles
-- ------------------------------------------------------------
INSERT INTO recruiters (user_id, company_name, department) VALUES
(1, 'TechCorp Solutions', 'Engineering'),
(2, 'Innovate Labs', 'Human Resources');

-- ------------------------------------------------------------
-- Candidate profiles (resume_text is a short plain-text summary used
-- for keyword-based screening)
-- ------------------------------------------------------------
INSERT INTO candidates (user_id, phone, skills, education, experience_years, resume_text) VALUES
(3, '9876543210', 'Java, Spring Boot, MySQL, REST API, Git',
 'B.E. Computer Science, Pune University', 3,
 'Experienced Java backend developer with 3 years building REST APIs using Spring Boot and MySQL. Familiar with Git workflows and unit testing.'),
(4, '9876543211', 'Python, Django, PostgreSQL, Docker, AWS',
 'B.Tech Information Technology, VIT', 2,
 'Python developer with 2 years experience in Django web applications, PostgreSQL, and deploying containerized apps with Docker on AWS.'),
(5, '9876543212', 'Java, Swing, JDBC, MySQL, OOP',
 'B.E. Computer Engineering, Mumbai University', 1,
 'Fresh graduate with strong OOP fundamentals in Java, hands-on academic projects using Swing and JDBC with MySQL.'),
(6, '9876543213', 'JavaScript, React, Node.js, MongoDB, HTML, CSS',
 'B.Sc Computer Science, Delhi University', 4,
 'Full-stack JavaScript developer with 4 years of experience building React front-ends and Node.js/MongoDB backends.');

-- ------------------------------------------------------------
-- Jobs
-- ------------------------------------------------------------
INSERT INTO jobs (recruiter_id, title, description, required_skills, min_experience, location, status, posted_date) VALUES
(1, 'Java Backend Developer',
 'Looking for a Java developer to build and maintain REST APIs using Spring Boot and MySQL.',
 'Java, Spring Boot, MySQL, REST API', 2, 'Pune', 'OPEN', CURDATE()),
(1, 'Junior Java Developer',
 'Entry-level role for a Java/Swing developer to work on desktop applications with JDBC and MySQL.',
 'Java, Swing, JDBC, MySQL', 0, 'Pune', 'OPEN', CURDATE()),
(2, 'Full-Stack JavaScript Developer',
 'Full-stack role building React front-ends and Node.js backends with MongoDB.',
 'JavaScript, React, Node.js, MongoDB', 3, 'Bangalore', 'OPEN', CURDATE()),
(2, 'Python/Django Developer',
 'Backend developer role using Python, Django and PostgreSQL, with AWS deployment experience.',
 'Python, Django, PostgreSQL, AWS', 1, 'Remote', 'OPEN', CURDATE());

-- ------------------------------------------------------------
-- Sample applications (match_score values are illustrative starting points;
-- the app recalculates real scores whenever ApplicationService.applyToJob
-- or rescreenJob is called)
-- ------------------------------------------------------------
INSERT INTO applications (job_id, candidate_id, applied_date, status, match_score) VALUES
(1, 1, CURDATE(), 'APPLIED', 85.50),
(2, 3, CURDATE(), 'SHORTLISTED', 92.00),
(3, 4, CURDATE(), 'APPLIED', 88.75),
(4, 2, CURDATE(), 'APPLIED', 79.25);

-- ------------------------------------------------------------
-- Sample interview
-- ------------------------------------------------------------
INSERT INTO interviews (application_id, interview_date, interview_time, mode, status, feedback, rating) VALUES
(2, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '10:30:00', 'ONLINE', 'SCHEDULED', '', 0);
