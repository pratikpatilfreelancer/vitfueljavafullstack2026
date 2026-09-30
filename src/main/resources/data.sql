

-- Sample employees
INSERT IGNORE INTO employees (id, full_name, email, department, designation, date_of_joining) VALUES
(1, 'Prathamesh', 'prathamesh@gmail.com', 'Engineering', 'Software Engineer', '2022-06-15'),
(2, 'Omkar', 'omkar@gmail.com', 'Human Resources', 'HR Executive', '2021-03-10'),
(3, 'Yash', 'yash@gmail.com', 'Engineering', 'Senior Software Engineer', '2020-01-20'),
(4, 'Karan', 'karan@gmail.com', 'Finance', 'Accountant', '2023-02-05'),
(5, 'Om', 'om@gmail.com', 'Marketing', 'Marketing Analyst', '2022-11-01');

-- Sample leave requests (each references an employee_id above)
INSERT IGNORE INTO leave_requests (id, employee_id, leave_type, start_date, end_date, reason, status, applied_date) VALUES
(1, 1, 'Sick Leave', '2026-08-05', '2026-08-06', 'Fever and cold', 'APPROVED', '2026-08-03'),
(2, 1, 'Casual Leave', '2026-09-01', '2026-09-01', 'Personal work', 'PENDING', '2026-08-25'),
(3, 2, 'Earned Leave', '2026-08-20', '2026-08-25', 'Family vacation', 'PENDING', '2026-08-10'),
(4, 3, 'Sick Leave', '2026-07-15', '2026-07-16', 'Medical appointment', 'REJECTED', '2026-07-12'),
(5, 4, 'Casual Leave', '2026-08-28', '2026-08-28', 'Bank work', 'PENDING', '2026-08-26'),
(6, 5, 'Earned Leave', '2026-09-10', '2026-09-14', 'Wedding in family', 'APPROVED', '2026-08-20');
