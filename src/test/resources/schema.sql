-- User Level 테이블
CREATE TABLE IF NOT EXISTS account_level (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    level INT NOT NULL,
    required_study_time INT NOT NULL,
    character_name VARCHAR(255) NOT NULL,
    character_image VARCHAR(255) NOT NULL
);

-- 사용자 테이블
CREATE TABLE IF NOT EXISTS account (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    hashed_password VARCHAR(255),
    username VARCHAR(255) NOT NULL,
    total_study_time INT NOT NULL,
    account_level_id BIGINT NOT NULL,
    role VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (email),
    FOREIGN KEY (account_level_id) REFERENCES account_level (id)
);

-- 친구 관계 테이블
CREATE TABLE IF NOT EXISTS friend_relation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,    -- 관계 고유 ID
    requester_id BIGINT NOT NULL,            -- 친구 요청을 보낸 사용자의 ID
    friend_id BIGINT NOT NULL,               -- 친구 요청을 받은 사용자의 ID
    accepted BOOLEAN DEFAULT TRUE,           -- 친구 요청 수락 여부 (QR 코드를 사용하므로 기본값은 TRUE)
    created_at TIMESTAMP NOT NULL,           -- 친구 요청 생성 시간
    FOREIGN KEY (requester_id) REFERENCES account (id),  -- requester_id가 account 테이블의 id를 참조
    FOREIGN KEY (friend_id) REFERENCES account (id)      -- friend_id가 account 테이블의 id를 참조
);

-- 과목 테이블
CREATE TABLE IF NOT EXISTS subject (
    id BIGINT NOT NULL AUTO_INCREMENT,
    account_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (account_id, name),
    FOREIGN KEY (account_id) REFERENCES account (id)
);

-- 학습 세션 테이블
CREATE TABLE IF NOT EXISTS study_session (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    FOREIGN KEY (account_id) REFERENCES account (id),
    FOREIGN KEY (subject_id) REFERENCES subject (id)
);

-- D-Day 이벤트 테이블
CREATE TABLE IF NOT EXISTS d_day_event (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    event_name VARCHAR(255) NOT NULL,
    event_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (account_id) REFERENCES account (id)
);

CREATE TABLE IF NOT EXISTS tag (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS account_tag (
    id BIGINT NOT NULL AUTO_INCREMENT,
    account_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (account_id) REFERENCES account (id),
    FOREIGN KEY (tag_id) REFERENCES tag (id)
);
