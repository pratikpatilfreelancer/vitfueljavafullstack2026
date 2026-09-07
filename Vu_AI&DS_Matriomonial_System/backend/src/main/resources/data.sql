INSERT IGNORE INTO users (user_id, name, email, password) VALUES
(1, 'Sriyaan', 'sriyaan@example.com', '$2b$10$TpVRCNsQ1jAhSlvHmueDh.qp9Kft0gYUJoezwuYT6qOmUXcZ1KJa.'),
(2, 'Priya Patel', 'priya@example.com', '$2b$10$TpVRCNsQ1jAhSlvHmueDh.qp9Kft0gYUJoezwuYT6qOmUXcZ1KJa.'),
(3, 'Rohan Mehta', 'rohan@example.com', '$2b$10$TpVRCNsQ1jAhSlvHmueDh.qp9Kft0gYUJoezwuYT6qOmUXcZ1KJa.'),
(4, 'Ananya Singh', 'ananya@example.com', '$2b$10$TpVRCNsQ1jAhSlvHmueDh.qp9Kft0gYUJoezwuYT6qOmUXcZ1KJa.'),
(5, 'Vikram Reddy', 'vikram@example.com', '$2b$10$TpVRCNsQ1jAhSlvHmueDh.qp9Kft0gYUJoezwuYT6qOmUXcZ1KJa.'),
(6, 'Meera Nair', 'meera@example.com', '$2b$10$TpVRCNsQ1jAhSlvHmueDh.qp9Kft0gYUJoezwuYT6qOmUXcZ1KJa.'),
(7, 'Arjun Kapoor', 'arjun@example.com', '$2b$10$TpVRCNsQ1jAhSlvHmueDh.qp9Kft0gYUJoezwuYT6qOmUXcZ1KJa.'),
(8, 'Diya Joshi', 'diya@example.com', '$2b$10$TpVRCNsQ1jAhSlvHmueDh.qp9Kft0gYUJoezwuYT6qOmUXcZ1KJa.'),
(9, 'Kabir Malhotra', 'kabir@example.com', '$2b$10$TpVRCNsQ1jAhSlvHmueDh.qp9Kft0gYUJoezwuYT6qOmUXcZ1KJa.'),
(10, 'Isha Agarwal', 'isha@example.com', '$2b$10$TpVRCNsQ1jAhSlvHmueDh.qp9Kft0gYUJoezwuYT6qOmUXcZ1KJa.'),
(11, 'Siddharth Rao', 'siddharth@example.com', '$2b$10$TpVRCNsQ1jAhSlvHmueDh.qp9Kft0gYUJoezwuYT6qOmUXcZ1KJa.'),
(12, 'Nisha Verma', 'nisha@example.com', '$2b$10$TpVRCNsQ1jAhSlvHmueDh.qp9Kft0gYUJoezwuYT6qOmUXcZ1KJa.'),
(13, 'Aditi', 'aditi@example.com', '$2b$10$TpVRCNsQ1jAhSlvHmueDh.qp9Kft0gYUJoezwuYT6qOmUXcZ1KJa.');

INSERT IGNORE INTO profiles (profile_id, user_id, age, gender, city, education, occupation, about, photo_url) VALUES
(1, 1, 24, 'Male', 'Delhi', 'B.Tech CSE', 'Software Developer', 'Young and ambitious. Love coding, cricket, and weekend treks with friends.', '/images/profiles/sriyaan.jpg'),
(2, 2, 25, 'Female', 'Mumbai', 'MBA Finance', 'Financial Analyst', 'Foodie at heart. Love exploring cafes, reading fiction, and painting on lazy Sundays.', '/images/profiles/priya.jpg'),
(3, 3, 30, 'Male', 'Bangalore', 'M.Tech Electronics', 'Hardware Engineer', 'Music lover, amateur guitarist. Work hard, play hard. Weekend cricket with friends.', '/images/profiles/rohan.jpg'),
(4, 4, 27, 'Female', 'Delhi', 'B.Com', 'Chartered Accountant', 'Calm and career-driven. Morning walks, classical dance, home-cooked meals.', '/images/profiles/ananya.jpg'),
(5, 5, 29, 'Male', 'Hyderabad', 'B.Tech IT', 'Product Manager', 'Avid traveler — 12 countries and counting. Photography, street food, long drives.', '/images/profiles/vikram.jpg'),
(6, 6, 26, 'Female', 'Chennai', 'M.Sc Data Science', 'Data Analyst', 'Bookworm and coffee enthusiast. Slow mornings, podcasts, weekend getaways.', '/images/profiles/meera.jpg'),
(7, 7, 31, 'Male', 'Jaipur', 'MBA Marketing', 'Marketing Head', 'Fitness freak. Marathon runner. Dogs, cooking experiments, old Bollywood music.', '/images/profiles/arjun.jpg'),
(8, 8, 24, 'Female', 'Kolkata', 'B.A English Literature', 'Content Writer', 'Poetry lover. Journal every day. Rainy days, indie music, long conversations.', '/images/profiles/diya.jpg'),
(9, 9, 23, 'Male', 'Mumbai', 'B.Tech Mechanical', 'Automobile Engineer', 'Car enthusiast and Formula 1 fan. Weekends are for road trips and trying new dhabas.', '/images/profiles/kabir.jpg'),
(10, 10, 23, 'Female', 'Pune', 'B.Sc Nursing', 'Nurse', 'Compassionate and caring. Love gardening, baking, and spending time with family.', '/images/profiles/isha.jpg'),
(11, 11, 32, 'Male', 'Delhi', 'LLB', 'Lawyer', 'Argumentative by profession, chill by nature. Long walks in Lodhi Garden and street food.', '/images/profiles/siddharth.jpg'),
(12, 12, 26, 'Female', 'Bangalore', 'B.Des Fashion', 'Fashion Designer', 'Creative soul. Love colors, textures, weekend art exhibitions, and thrift shopping.', '/images/profiles/nisha.jpg'),
(13, 13, 23, 'Female', 'Mumbai', 'B.Tech CSE', 'Software Engineer', 'Creative thinker and coffee lover. Weekend hikes, board games, and exploring new places.', '/images/profiles/aditi.jpg');

INSERT IGNORE INTO interests (interest_id, sender_id, receiver_id, status) VALUES
(1, 1, 2, 'PENDING'),
(2, 3, 4, 'ACCEPTED'),
(3, 5, 6, 'PENDING'),
(4, 7, 2, 'PENDING'),
(5, 9, 10, 'PENDING'),
(6, 11, 12, 'PENDING'),
(7, 1, 13, 'PENDING'),
(8, 13, 1, 'ACCEPTED');
