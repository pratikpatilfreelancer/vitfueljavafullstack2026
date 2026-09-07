-- ============================================================
-- SmartHire - Useful reference queries
-- These are not run automatically; they're handy for demos,
-- debugging, and understanding the data model.
-- ============================================================

USE smarthire;

-- All open jobs with recruiter/company info
SELECT j.job_id, j.title, j.location, j.min_experience, j.required_skills,
       r.company_name
FROM jobs j
JOIN recruiters r ON j.recruiter_id = r.recruiter_id
WHERE j.status = 'OPEN'
ORDER BY j.posted_date DESC;

-- All applicants for a specific job, ranked by match score
SELECT a.application_id, u.full_name AS candidate_name, a.match_score, a.status, a.applied_date
FROM applications a
JOIN candidates c ON a.candidate_id = c.candidate_id
JOIN users u ON c.user_id = u.user_id
WHERE a.job_id = 1
ORDER BY a.match_score DESC;

-- A candidate's full application history
SELECT j.title, a.applied_date, a.status, a.match_score
FROM applications a
JOIN jobs j ON a.job_id = j.job_id
WHERE a.candidate_id = 1
ORDER BY a.applied_date DESC;

-- Upcoming interviews for a recruiter
SELECT i.interview_date, i.interview_time, i.mode, u.full_name AS candidate_name, j.title AS job_title
FROM interviews i
JOIN applications a ON i.application_id = a.application_id
JOIN jobs j ON a.job_id = j.job_id
JOIN candidates c ON a.candidate_id = c.candidate_id
JOIN users u ON c.user_id = u.user_id
WHERE j.recruiter_id = 1 AND i.status = 'SCHEDULED'
ORDER BY i.interview_date, i.interview_time;

-- Applications grouped by status, per recruiter
SELECT j.recruiter_id, a.status, COUNT(*) AS total
FROM applications a
JOIN jobs j ON a.job_id = j.job_id
GROUP BY j.recruiter_id, a.status;

-- Average match score per job
SELECT j.title, ROUND(AVG(a.match_score), 2) AS avg_match_score, COUNT(a.application_id) AS applicant_count
FROM jobs j
LEFT JOIN applications a ON j.job_id = a.job_id
GROUP BY j.job_id, j.title
ORDER BY avg_match_score DESC;

-- Candidates who have never applied to anything
SELECT u.full_name, u.email
FROM candidates c
JOIN users u ON c.user_id = u.user_id
LEFT JOIN applications a ON c.candidate_id = a.candidate_id
WHERE a.application_id IS NULL;

-- Jobs with no applicants yet
SELECT j.title, j.posted_date
FROM jobs j
LEFT JOIN applications a ON j.job_id = a.job_id
WHERE a.application_id IS NULL AND j.status = 'OPEN';
