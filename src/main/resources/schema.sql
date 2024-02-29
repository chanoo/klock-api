-- User Level 테이블
CREATE TABLE IF NOT EXISTS klk_user_level
(
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    level               INT          NOT NULL,
    required_study_time INT          NOT NULL,
    character_name      VARCHAR(255) NOT NULL,
    character_image     VARCHAR(255) NOT NULL
);

-- 사용자 테이블
CREATE TABLE IF NOT EXISTS klk_user
(
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    email            VARCHAR(255) NOT NULL,
    hashed_password  VARCHAR(255),
    nickname         VARCHAR(255) NOT NULL,
    total_study_time INT          NOT NULL,
    user_level_id    BIGINT       NOT NULL,
    profile_image    VARCHAR(255),
    role             VARCHAR(255) NOT NULL,
    active           BOOLEAN      NOT NULL,
    created_at       TIMESTAMP    NOT NULL,
    updated_at       TIMESTAMP    NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (email),
    FOREIGN KEY (user_level_id) REFERENCES klk_user_level (id)
);

-- 소셜 로그인 테이블
CREATE TABLE IF NOT EXISTS klk_social_login
(
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    provider         VARCHAR(255) NOT NULL,
    provider_user_id VARCHAR(255) NOT NULL,
    user_id          BIGINT       NOT NULL,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE (provider, provider_user_id),
    FOREIGN KEY (user_id) REFERENCES klk_user (id)
);

-- 친구 관계 테이블
CREATE TABLE IF NOT EXISTS klk_friend_relation
(
    id           BIGINT    NOT NULL AUTO_INCREMENT,      -- 관계 고유 ID
    user_id      BIGINT    NOT NULL,                     -- 사용자의 ID
    follow_id    BIGINT    NOT NULL,                     -- 팔로우 요청을 한 사용자의 ID
    followed     BOOLEAN   DEFAULT TRUE,                 -- 팔로우 요청 수락 여부 (QR 코드를 사용하므로 기본값은 TRUE)
    created_at   TIMESTAMP NOT NULL,                     -- 친구 요청 생성 시간
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES klk_user (id),      -- requester_id가 app_user 테이블의 id를 참조
    FOREIGN KEY (follow_id) REFERENCES klk_user (id)     -- friend_id가 app_user 테이블의 id를 참조
);

-- 과목 테이블
CREATE TABLE IF NOT EXISTS klk_subject
(
    id      BIGINT       NOT NULL AUTO_INCREMENT,  -- 고유 식별자
    user_id BIGINT       NOT NULL,                 -- 사용자 ID (외래 키)
    name    VARCHAR(255) NOT NULL,                 -- 과목 이름
    PRIMARY KEY (id),                              -- 기본 키 설정
    UNIQUE (user_id, name),                        -- 사용자별 중복 방지
    FOREIGN KEY (user_id) REFERENCES klk_user (id) -- app_user 테이블 참조
);

-- 학습 세션 테이블
CREATE TABLE IF NOT EXISTS klk_study_session
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,        -- 고유 식별자
    user_id    BIGINT       NOT NULL,                    -- 사용자 ID (외래 키)
    start_time TIMESTAMP    NOT NULL,                    -- 학습 시작 시간
    end_time   TIMESTAMP    NOT NULL,                    -- 학습 종료 시간
    timer_name VARCHAR(25%) NOT NULL,                    -- 타이머 이름
    timer_type VARCHAR(25%) NOT NULL,                    -- 타이머 종류
    FOREIGN KEY (user_id) REFERENCES klk_user (id)       -- app_user 테이블 참조
);

-- D-Day 이벤트 테이블
CREATE TABLE IF NOT EXISTS klk_d_day_event
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 고유 식별자
    user_id    BIGINT       NOT NULL,              -- 사용자 ID (외래 키)
    event_name VARCHAR(255) NOT NULL,              -- 이벤트 이름
    event_date DATE         NOT NULL,              -- 이벤트 날짜
    created_at TIMESTAMP    NOT NULL,              -- 생성일
    FOREIGN KEY (user_id) REFERENCES klk_user (id) -- app_user 테이블 참조
);

-- 태그 테이블
CREATE TABLE IF NOT EXISTS klk_tag
(
    id   BIGINT       NOT NULL AUTO_INCREMENT, -- 고유 식별자
    name VARCHAR(255) NOT NULL,                -- 태그 이름
    PRIMARY KEY (id),                          -- 기본 키 설정
    UNIQUE (name)                              -- 이름 중복 방지
);

-- 사용자별 태그 매핑 테이블
CREATE TABLE IF NOT EXISTS klk_user_tag
(
    id      BIGINT NOT NULL AUTO_INCREMENT,         -- 고유 식별자
    user_id BIGINT NOT NULL,                        -- 사용자 ID (외래 키)
    tag_id  BIGINT NOT NULL,                        -- 태그 ID (외래 키)
    PRIMARY KEY (id),                               -- 기본 키 설정
    FOREIGN KEY (user_id) REFERENCES klk_user (id), -- app_user 테이블 참조
    FOREIGN KEY (tag_id) REFERENCES klk_tag (id)    -- tag 테이블 참조
);

-- 챗봇 테이블 생성
CREATE TABLE IF NOT EXISTS klk_chat_bot
(
    id                 BIGINT       NOT NULL AUTO_INCREMENT,                                        -- 고유 식별자
    subject            VARCHAR(255) NOT NULL,                                                       -- 챗봇 주제
    name               VARCHAR(255) NOT NULL,                                                       -- 챗봇 이름
    chat_bot_image_url VARCHAR(255) NOT NULL,                                                       -- 챗봇 이미지 URL
    title              VARCHAR(255) NOT NULL,                                                       -- 챗봇 타이틀
    persona            VARCHAR(255) NOT NULL,                                                       -- 챗봇 개성
    active             BOOLEAN      NOT NULL,                                                       -- 챗봇 활성 상태
    created_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,                             -- 생성일
    updated_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 업데이트 일자
    PRIMARY KEY (id)                                                                                -- 기본 키 설정
);

-- 타이머 시험 테이블 생성
CREATE TABLE IF NOT EXISTS klk_timer_exam
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,                                           -- 고유 식별자
    user_id        BIGINT       NOT NULL,                                                       -- 사용자 ID (외래 키)
    name           VARCHAR(255) NOT NULL,                                                       -- 시험 이름
    seq            INT          NOT NULL,                                                       -- 정렬
    start_time     TIMESTAMP    NOT NULL,                                                       -- 시작 시간
    duration       INT          NOT NULL,                                                       -- 지속 시간
    question_count INT          NOT NULL,                                                       -- 문제 수
    marking_time   INT          NOT NULL DEFAULT 0,                                             -- 마킹 시간
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,                             -- 생성일
    updated_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 업데이트 일자
    FOREIGN KEY (user_id) REFERENCES klk_user (id)                                              -- 사용자 타이머 테이블 참조
);

-- 타이머 포모도로 테이블 생성
CREATE TABLE IF NOT EXISTS klk_timer_pomodoro
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,                                           -- 고유 식별자
    user_id     BIGINT       NOT NULL,                                                       -- 사용자 ID (외래 키)
    name        VARCHAR(255) NOT NULL,                                                       -- 포모도로 이름
    seq         INT          NOT NULL,                                                       -- 정렬
    focus_time  INT          NOT NULL,                                                       -- 집중 시간
    break_time  INT          NOT NULL,                                                       -- 휴식 시간
    cycle_count INT          NOT NULL,                                                       -- 사이클 수
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,                             -- 생성일
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 업데이트 일자
    FOREIGN KEY (user_id) REFERENCES klk_user (id)                                           -- 사용자 타이머 테이블 참조
);

-- 타이머 공부 테이블 생성
CREATE TABLE IF NOT EXISTS klk_timer_focus
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,                                           -- 고유 식별자
    user_id    BIGINT       NOT NULL,                                                       -- 사용자 ID (외래 키)
    name       VARCHAR(255) NOT NULL,                                                       -- 공부 이름
    seq        INT          NOT NULL,                                                       -- 정렬
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,                             -- 생성일
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 업데이트 일자
    FOREIGN KEY (user_id) REFERENCES klk_user (id)                                          -- 사용자 타이머 테이블 참조
);

-- User Trace 테이블
CREATE TABLE IF NOT EXISTS klk_user_trace
(
    id                      BIGINT       NOT NULL AUTO_INCREMENT,
    user_id                 BIGINT       NOT NULL,                                          -- 담벼락 사용자 ID (외래 키)
    write_user_id           BIGINT       NOT NULL,                                          -- 작성자 ID
    type                    VARCHAR(12)  NOT NULL,                                              -- 내용
    contents                VARCHAR(255) NULL,                                              -- 내용
    contents_image          VARCHAR(255) NULL,                                              -- 내용 이미지
    heart_count             INT          NOT NULL DEFAULT 0,                                -- 좋아요 갯수
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,                             -- 생성일
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES klk_user (id)
);

-- User Trace Heart 테이블
CREATE TABLE IF NOT EXISTS klk_user_trace_heart
(
    id                      BIGINT       NOT NULL AUTO_INCREMENT,
    user_trace_id           BIGINT       NOT NULL,                                          -- 담벼락 ID (외래 키)
    user_id                 BIGINT       NOT NULL,                                          -- 담벼락 사용자 ID (외래 키)
    heart_count             INT          NOT NULL,                                          -- 좋아요 갯수
    PRIMARY KEY (id),
    FOREIGN KEY (user_trace_id) REFERENCES klk_user_trace (id),
    FOREIGN KEY (user_id) REFERENCES klk_user (id)
);

-- 타이머 자동시간 테이블 생성
CREATE TABLE IF NOT EXISTS klk_timer_auto
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,                                           -- 고유 식별자
    user_id    BIGINT       NOT NULL,                                                       -- 사용자 ID (외래 키)
    name       VARCHAR(255) NOT NULL,                                                       -- 공부 이름
    seq        INT          NOT NULL,                                                       -- 정렬
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,                             -- 생성일
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 업데이트 일자
    FOREIGN KEY (user_id) REFERENCES klk_user (id)                                          -- 사용자 타이머 테이블 참조
);