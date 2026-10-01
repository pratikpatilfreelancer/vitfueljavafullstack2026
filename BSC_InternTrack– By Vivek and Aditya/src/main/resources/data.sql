INSERT INTO students (student_id, name, email, phone, college, course, year, resume_url, created_at) VALUES
('student-001', 'Alice Johnson', 'alice@example.com', '9876543210', 'ABC College', 'Computer Science', 3, 'https://example.com/resumes/alice.pdf', NOW()),
('student-002', 'Bob Smith', 'bob@example.com', '9876543211', 'XYZ University', 'Information Technology', 4, 'https://example.com/resumes/bob.pdf', NOW()),
('student-003', 'Carol White', 'carol@example.com', '9876543212', 'LMN Institute', 'Computer Science', 2, 'https://example.com/resumes/carol.pdf', NOW()),
('student-004', 'David Brown', 'david@example.com', '9876543213', 'PQR College', 'Electronics', 3, 'https://example.com/resumes/david.pdf', NOW()),
('student-005', 'Eve Davis', 'eve@example.com', '9876543214', 'DEF University', 'Computer Science', 4, 'https://example.com/resumes/eve.pdf', NOW());

INSERT INTO companies (company_id, company_name, location, industry, website, contact_email, created_at) VALUES
('company-001', 'TechCorp Solutions', 'Bangalore', 'Software', 'https://techcorp.com', 'hr@techcorp.com', NOW()),
('company-002', 'DataWave Inc', 'Hyderabad', 'Data Analytics', 'https://datawave.com', 'careers@datawave.com', NOW()),
('company-003', 'CloudSys', 'Chennai', 'Cloud Services', 'https://cloudsys.com', 'jobs@cloudsys.com', NOW()),
('company-004', 'NextGen Tech', 'Pune', 'AI/ML', 'https://nextgentech.com', 'recruit@nextgentech.com', NOW()),
('company-005', 'SoftBridge Labs', 'Mumbai', 'Software', 'https://softbridge.com', 'hr@softbridge.com', NOW());

INSERT INTO users (user_id, email, password, role, ref_id, created_at) VALUES
('user-001', 'alice@example.com', '$2a$10$ho6/66n3y9OyLTR6j2NFqelY0.61hedLI8tNDxOQAbKvEVRa4mLxW', 'STUDENT', 'student-001', NOW()),
('user-002', 'bob@example.com', '$2a$10$ho6/66n3y9OyLTR6j2NFqelY0.61hedLI8tNDxOQAbKvEVRa4mLxW', 'STUDENT', 'student-002', NOW()),
('user-003', 'carol@example.com', '$2a$10$ho6/66n3y9OyLTR6j2NFqelY0.61hedLI8tNDxOQAbKvEVRa4mLxW', 'STUDENT', 'student-003', NOW()),
('user-004', 'hr@techcorp.com', '$2a$10$ho6/66n3y9OyLTR6j2NFqelY0.61hedLI8tNDxOQAbKvEVRa4mLxW', 'COMPANY', 'company-001', NOW()),
('user-005', 'careers@datawave.com', '$2a$10$ho6/66n3y9OyLTR6j2NFqelY0.61hedLI8tNDxOQAbKvEVRa4mLxW', 'COMPANY', 'company-002', NOW());

INSERT INTO notifications (notification_id, user_id, message, type, is_read, created_at) VALUES
('notif-001', 'user-001', 'Your application for Software Developer Intern has been Shortlisted', 'STATUS_UPDATE', FALSE, NOW()),
('notif-002', 'user-001', 'Your application for Frontend Developer Intern is now in Interview stage', 'STATUS_UPDATE', FALSE, NOW()),
('notif-003', 'user-003', 'Congratulations! Your application for Frontend Developer Intern has been Selected', 'STATUS_UPDATE', TRUE, NOW());

INSERT INTO internships (internship_id, company_id, title, description, location, duration, stipend, application_deadline, created_at) VALUES
('intern-001', 'company-001', 'Software Developer Intern', 'Develop web applications using Spring Boot', 'Bangalore', '3 Months', 15000.00, '2026-10-30', NOW()),
('intern-002', 'company-002', 'Data Analyst Intern', 'Analyze datasets and build dashboards', 'Hyderabad', '2 Months', 12000.00, '2026-11-15', NOW()),
('intern-003', 'company-003', 'Cloud Intern', 'Assist with AWS deployments', 'Chennai', '3 Months', 18000.00, '2026-10-25', NOW()),
('intern-004', 'company-001', 'Frontend Developer Intern', 'Build responsive UI with JavaScript', 'Bangalore', '2 Months', 14000.00, '2026-11-01', NOW()),
('intern-005', 'company-004', 'Machine Learning Intern', 'Work on ML model training and evaluation', 'Pune', '4 Months', 20000.00, '2026-12-01', NOW()),
('intern-006', 'company-005', 'Full Stack Intern', 'Develop features across frontend and backend', 'Mumbai', '3 Months', 16000.00, '2026-11-20', NOW()),
('intern-007', 'company-002', 'Backend Developer Intern', 'Design REST APIs and database schemas', 'Hyderabad', '2 Months', 13000.00, '2026-10-28', NOW()),
('intern-008', 'company-003', 'DevOps Intern', 'Automate CI/CD pipelines', 'Chennai', '3 Months', 17000.00, '2026-11-10', NOW());

INSERT INTO skills (skill_id, skill_name, category, created_at) VALUES
('skill-001', 'Java', 'Programming', NOW()),
('skill-002', 'Python', 'Programming', NOW()),
('skill-003', 'JavaScript', 'Programming', NOW()),
('skill-004', 'C++', 'Programming', NOW()),
('skill-005', 'Spring Boot', 'Framework', NOW()),
('skill-006', 'Django', 'Framework', NOW()),
('skill-007', 'React', 'Framework', NOW()),
('skill-008', 'Angular', 'Framework', NOW()),
('skill-009', 'MySQL', 'Database', NOW()),
('skill-010', 'PostgreSQL', 'Database', NOW()),
('skill-011', 'AWS', 'Cloud', NOW()),
('skill-012', 'Docker', 'Cloud', NOW()),
('skill-013', 'Linux', 'Cloud', NOW()),
('skill-014', 'Machine Learning', 'AI/ML', NOW()),
('skill-015', 'TensorFlow', 'AI/ML', NOW()),
('skill-016', 'Git', 'Tools', NOW()),
('skill-017', 'Jenkins', 'Tools', NOW()),
('skill-018', 'HTML', 'Frontend', NOW()),
('skill-019', 'CSS', 'Frontend', NOW()),
('skill-020', 'SQL', 'Database', NOW());

INSERT INTO student_skills (student_id, skill_id, created_at) VALUES
('student-001', 'skill-001', NOW()),
('student-001', 'skill-005', NOW()),
('student-001', 'skill-009', NOW()),
('student-002', 'skill-002', NOW()),
('student-002', 'skill-006', NOW()),
('student-002', 'skill-010', NOW()),
('student-003', 'skill-003', NOW()),
('student-003', 'skill-007', NOW()),
('student-003', 'skill-013', NOW()),
('student-004', 'skill-004', NOW()),
('student-004', 'skill-002', NOW()),
('student-004', 'skill-014', NOW()),
('student-005', 'skill-001', NOW()),
('student-005', 'skill-008', NOW()),
('student-005', 'skill-009', NOW());

INSERT INTO internship_skills (internship_id, skill_id, created_at) VALUES
('intern-001', 'skill-001', NOW()),
('intern-001', 'skill-005', NOW()),
('intern-001', 'skill-009', NOW()),
('intern-002', 'skill-002', NOW()),
('intern-002', 'skill-020', NOW()),
('intern-003', 'skill-011', NOW()),
('intern-003', 'skill-012', NOW()),
('intern-003', 'skill-013', NOW()),
('intern-004', 'skill-018', NOW()),
('intern-004', 'skill-019', NOW()),
('intern-004', 'skill-003', NOW()),
('intern-005', 'skill-002', NOW()),
('intern-005', 'skill-014', NOW()),
('intern-005', 'skill-015', NOW()),
('intern-006', 'skill-001', NOW()),
('intern-006', 'skill-008', NOW()),
('intern-006', 'skill-009', NOW()),
('intern-007', 'skill-001', NOW()),
('intern-007', 'skill-005', NOW()),
('intern-007', 'skill-009', NOW()),
('intern-008', 'skill-012', NOW()),
('intern-008', 'skill-016', NOW()),
('intern-008', 'skill-017', NOW());

INSERT INTO applications (application_id, student_id, internship_id, application_date, status, notes, created_at, updated_at) VALUES
('app-001', 'student-001', 'intern-001', '2026-09-01', 'Shortlisted', 'Good resume', NOW(), NOW()),
('app-002', 'student-001', 'intern-004', '2026-09-03', 'Interview', 'Technical round scheduled', NOW(), NOW()),
('app-003', 'student-002', 'intern-002', '2026-09-02', 'Applied', 'Awaiting response', NOW(), NOW()),
('app-004', 'student-002', 'intern-005', '2026-09-04', 'Rejected', 'Not selected', NOW(), NOW()),
('app-005', 'student-003', 'intern-004', '2026-09-05', 'Selected', 'Offer letter sent', NOW(), NOW()),
('app-006', 'student-003', 'intern-006', '2026-09-06', 'Interview', 'HR round pending', NOW(), NOW()),
('app-007', 'student-004', 'intern-003', '2026-09-07', 'Applied', 'Submitted today', NOW(), NOW()),
('app-008', 'student-004', 'intern-005', '2026-09-08', 'Applied', 'Submitted today', NOW(), NOW()),
('app-009', 'student-005', 'intern-001', '2026-09-09', 'Shortlisted', 'Resume shortlisted', NOW(), NOW()),
('app-010', 'student-005', 'intern-007', '2026-09-10', 'Applied', 'Awaiting response', NOW(), NOW());
