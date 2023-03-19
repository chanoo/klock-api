-- account_level 더미 데이터
INSERT INTO account_level (level, required_study_time, character_name, character_image)
VALUES
    (1, 0, 'Beginner', 'beginner.jpg'),
    (2, 100, 'Student', 'student.jpg'),
    (3, 200, 'Scholar', 'scholar.jpg'),
    (4, 300, 'Master', 'master.jpg');

-- account 더미 데이터
INSERT INTO account (email, hashed_password, username, total_study_time, account_level_id, role, active, created_at, updated_at)
VALUES
    ('user1@example.com', 'password1', 'user1', 50, 1, 'USER', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('user2@example.com', 'password2', 'user2', 150, 2, 'USER', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('user3@example.com', 'password3', 'user3', 250, 3, 'USER', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('admin@example.com', 'adminpassword', 'admin', 0, 4, 'ADMIN', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO tag (name) VALUES ('초등'),('중1'),('중2'),('중3'),('고1'),('고2'),('고3'),('N수생'),('대학생'),('편입'),('어학'),('자격증'),('수험생'),('취준생'),('고시생'),('의대/의전원'),('PEET'),('로스쿨'),('이직'),('자기계발'),('기타');
