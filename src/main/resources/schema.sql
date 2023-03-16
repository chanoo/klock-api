-- User Level 테이블
CREATE TABLE `account_level` (
    `id` BIGINT(20) AUTO_INCREMENT PRIMARY KEY,
    `level` INT NOT NULL,
    `required_study_time` INT NOT NULL,
    `character_name` VARCHAR(255) NOT NULL,
    `character_image` VARCHAR(255) NOT NULL
);

-- 사용자 테이블
CREATE TABLE `account` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `email` VARCHAR(255) NOT NULL,
    `hashed_password` VARCHAR(255) NOT NULL,
    `username` VARCHAR(255) NOT NULL,
    `total_study_time` INT NOT NULL,
    `acount_level_id` BIGINT(20) NOT NULL,
    `role` ENUM('USER', 'ADMIN') NOT NULL,
    `active` BOOLEAN NOT NULL,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `email_UNIQUE` (`email` ASC),
    FOREIGN KEY (`acount_level_id`) REFERENCES `account_level` (`id`)
);

-- 과목 테이블
CREATE TABLE `subject` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `account_id` BIGINT(20) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `account_subject_UNIQUE` (`account_id`, `name` ASC),
    FOREIGN KEY (`account_id`) REFERENCES `account` (`id`)
);

-- 학습 세션 테이블
CREATE TABLE `study_session` (
     `id` BIGINT(20) AUTO_INCREMENT PRIMARY KEY,
     `account_id` BIGINT(20) NOT NULL,
     `subject_id` BIGINT(20) NOT NULL,
     `start_time` DATETIME NOT NULL,
     `end_time` DATETIME NOT NULL,
     FOREIGN KEY (`account_id`) REFERENCES `account` (`id`),
     FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`)
);

-- D-Day 이벤트 테이블
CREATE TABLE `d_day_event` (
    `id` BIGINT(20) AUTO_INCREMENT PRIMARY KEY,
    `account_id` BIGINT(20) NOT NULL,
    `event_name` VARCHAR(255) NOT NULL,
    `event_date` DATE NOT NULL,
    `created_at` DATETIME NOT NULL,
    FOREIGN KEY (`account_id`) REFERENCES `account` (`id`)
);


CREATE TABLE `tag` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `name_UNIQUE` (`name` ASC)
);

CREATE TABLE `account_tag` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `account_id` BIGINT(20) NOT NULL,
    `tag_id` BIGINT(20) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_account_tag_account`
        FOREIGN KEY (`account_id`)
            REFERENCES `account` (`id`),
    CONSTRAINT `fk_account_tag_tag`
        FOREIGN KEY (`tag_id`)
            REFERENCES `tag` (`id`)
);

-- account_level 더미 데이터
INSERT INTO `account_level` (`level`, `required_study_time`, `character_name`, `character_image`)
VALUES
    (1, 0, 'Beginner', 'beginner.jpg'),
    (2, 100, 'Student', 'student.jpg'),
    (3, 200, 'Scholar', 'scholar.jpg'),
    (4, 300, 'Master', 'master.jpg');

-- account 더미 데이터
INSERT INTO `account` (`email`, `hashed_password`, `username`, `total_study_time`, `acount_level_id`, `role`, `active`, `created_at`, `updated_at`)
VALUES
    ('user1@example.com', 'password1', 'user1', 50, 1, 'USER', 1, NOW(), NOW()),
    ('user2@example.com', 'password2', 'user2', 150, 2, 'USER', 1, NOW(), NOW()),
    ('user3@example.com', 'password3', 'user3', 250, 3, 'USER', 1, NOW(), NOW()),
    ('admin@example.com', 'adminpassword', 'admin', 0, 4, 'ADMIN', 1, NOW(), NOW());

INSERT INTO tag (name) VALUES ('초등'),('중1'),('중2'),('중3'),('고1'),('고2'),('고3'),('N수생'),('대학생'),('편입'),('어학'),('자격증'),('수험생'),('취준생'),('고시생'),('의대/의전원'),('PEET'),('로스쿨'),('이직'),('자기계발'),('기타');
