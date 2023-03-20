# Spring Boot + WebFlux + R2DBC + MySQL + Kotlin + WebSocket + Gradle + JWT + Test

메인:
```bash
main/kotlin/app.klock.api
├── domain
│   └── entity
├── exception
├── functional
│   ├── account
│   ├── auth
│   ├── dDayEvent
│   ├── studySession
│   └── tag
├── repository
├── security
├── service
├── utils
└── websocket
    ├── config
    └── handler
```

테스트:
```bash
text/kotlin/app.klock.api
├── config
├── functional
└── service
```

## 인증
1. [x] 이메일, 비밀번호 로그인
2. [x] 페이스북, 애플 소셜 로그인
3. [x] 회원 가입
4. [x] 토큰 갱신

## 회원
1. [x] 계정 정보 수정
2. [x] 계정 삭제
3. [x] 계정 정보 조회
4. [x] 비밀번호 변경

## 공부 시간
1. [x] 공부 시간 등록 
2. [x] 공부 시간 수정
3. [x] 나의 특정기간 공부 시간 조회

## D-Day
1. [x] 나의 D-Day 등록
2. [x] 나의 D-Day 전체 조회
3. [x] 나의 D-Day 삭제
4. [x] 나의 D-Day 수정
5. [x] 나의 D-Day 조회

## 태그
1. [x] 나의 태그 다량 등록
2. [x] 나의 태그 전체 삭제
3. [x] 나의 태그 조회

## WebSocket
1. [x] 접속자 수 조회

## Todo
1. RSocket
2. 리액티브 프로그래밍 스타일을 가독성을 위해 코루틴 스파일로 변경
