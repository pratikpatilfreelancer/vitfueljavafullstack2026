-- Example queries covering the rest of the SQL syllabus section:
-- SELECT with WHERE, UPDATE, DELETE, Inner/Outer/Self/Cross JOIN.
-- Run these against vanguard_db after schema.sql and after the app
-- has written some data via IncidentDAO/ResourceDAO.

-- SELECT with WHERE clause
SELECT * FROM incidents WHERE severity = 'CRITICAL';

-- UPDATE
UPDATE incidents SET status = 'RESOLVED' WHERE id = 'i1';

-- DELETE
DELETE FROM incidents WHERE status = 'RESOLVED' AND reported_at < NOW() - INTERVAL 7 DAY;

-- INNER JOIN: every assigned incident with its resource's details
SELECT i.id, i.description, r.name, r.type
FROM incidents i
INNER JOIN resources r ON i.assigned_resource_id = r.id;

-- LEFT (OUTER) JOIN: every incident, even ones with no resource assigned yet
SELECT i.id, i.description, r.name AS assigned_resource
FROM incidents i
LEFT JOIN resources r ON i.assigned_resource_id = r.id;

-- SELF JOIN: pair up resources of the same type stationed near each other
SELECT a.name AS resource_a, b.name AS resource_b, a.type
FROM resources a
JOIN resources b ON a.type = b.type AND a.id < b.id;

-- CROSS JOIN: every possible (incident, resource) pairing —
-- illustrative only, this is the "brute force" set the allocation
-- algorithm in AllocationService.java narrows down using Streams.
SELECT i.id AS incident_id, r.id AS resource_id
FROM incidents i
CROSS JOIN resources r
WHERE i.status = 'REPORTED' AND r.status = 'AVAILABLE';
