-- User Level 테이블
CREATE TABLE IF NOT EXISTS account_level (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 고유 식별자
    level INT NOT NULL, -- 레벨
    required_study_time INT NOT NULL, -- 해당 레벨을 달성하기 위해 필요한 학습 시간
    character_name VARCHAR(255) NOT NULL, -- 캐릭터 이름
    character_image VARCHAR(255) NOT NULL -- 캐릭터 이미지 경로
);

-- 사용자 테이블
CREATE TABLE IF NOT EXISTS account (
    id BIGINT NOT NULL AUTO_INCREMENT, -- 고유 식별자
    email VARCHAR(255) NOT NULL, -- 이메일
    hashed_password VARCHAR(255), -- 해시된 비밀번호
    username VARCHAR(255) NOT NULL, -- 사용자 이름
    total_study_time INT NOT NULL, -- 누적 학습 시간
    account_level_id BIGINT NOT NULL, -- 사용자 레벨 ID (외래 키)
    role VARCHAR(255) NOT NULL, -- 사용자 역할
    active BOOLEAN NOT NULL, -- 사용자 활성화 여부
    created_at TIMESTAMP NOT NULL, -- 생성일
    updated_at TIMESTAMP NOT NULL, -- 업데이트 일자
    PRIMARY KEY (id), -- 기본 키 설정
    UNIQUE (email), -- 이메일 중복 방지
    FOREIGN KEY (account_level_id) REFERENCES account_level (id) -- account_level 테이블 참조
);

-- 소셜 로그인 테이블
CREATE TABLE IF NOT EXISTS social_login (
    id BIGINT NOT NULL AUTO_INCREMENT, -- 고유 식별자
    provider VARCHAR(255) NOT NULL, -- 소셜 로그인 제공자 (예: Facebook, Google, Twitter 등)
    provider_user_id VARCHAR(255) NOT NULL, -- 제공자의 사용자 고유 식별자
    account_id BIGINT NOT NULL, -- 연결된 사용자 계정 ID (외래 키)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 생성일
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 업데이트 일자
    PRIMARY KEY (id), -- 기본 키 설정
    FOREIGN KEY (account_id) REFERENCES account (id) -- account 테이블 참조
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
    id BIGINT NOT NULL AUTO_INCREMENT, -- 고유 식별자
    account_id BIGINT NOT NULL, -- 사용자 ID (외래 키)
    name VARCHAR(255) NOT NULL, -- 과목 이름
    PRIMARY KEY (id), -- 기본 키 설정
    UNIQUE (account_id, name), -- 사용자별 중복 방지
    FOREIGN KEY (account_id) REFERENCES account (id) -- account 테이블 참조
);

-- 학습 세션 테이블
CREATE TABLE IF NOT EXISTS study_session (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 고유 식별자
    account_id BIGINT NOT NULL, -- 사용자 ID (외래 키)
    subject_id BIGINT NOT NULL, -- 과목 ID (외래 키)
    start_time TIMESTAMP NOT NULL, -- 학습 시작 시간
    end_time TIMESTAMP NOT NULL, -- 학습 종료 시간
    FOREIGN KEY (account_id) REFERENCES account (id), -- account 테이블 참조
    FOREIGN KEY (subject_id) REFERENCES subject (id) -- subject 테이블 참조
);

-- D-Day 이벤트 테이블
CREATE TABLE IF NOT EXISTS d_day_event (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 고유 식별자
    account_id BIGINT NOT NULL, -- 사용자 ID (외래 키)
    event_name VARCHAR(255) NOT NULL, -- 이벤트 이름
    event_date DATE NOT NULL, -- 이벤트 날짜
    created_at TIMESTAMP NOT NULL, -- 생성일
    FOREIGN KEY (account_id) REFERENCES account (id) -- account 테이블 참조
);
-- 태그 테이블
CREATE TABLE IF NOT EXISTS tag (
    id BIGINT NOT NULL AUTO_INCREMENT, -- 고유 식별자
    name VARCHAR(255) NOT NULL, -- 태그 이름
    PRIMARY KEY (id), -- 기본 키 설정
    UNIQUE (name) -- 이름 중복 방지
);

-- 사용자별 태그 매핑 테이블
CREATE TABLE IF NOT EXISTS account_tag (
    id BIGINT NOT NULL AUTO_INCREMENT, -- 고유 식별자
    account_id BIGINT NOT NULL, -- 사용자 ID (외래 키)
    tag_id BIGINT NOT NULL, -- 태그 ID (외래 키)
    PRIMARY KEY (id), -- 기본 키 설정
    FOREIGN KEY (account_id) REFERENCES account (id), -- account 테이블 참조
    FOREIGN KEY (tag_id) REFERENCES tag (id) -- tag 테이블 참조
);
