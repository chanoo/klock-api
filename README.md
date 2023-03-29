# Spring Boot + WebFlux + R2DBC + MySQL + Kotlin + WebSocket + Gradle + JWT + Test

## What is this?

* Spring Boot
* Spring WebFlux
* Spring Security
* Spring Data R2DBC
* Spring Test
* Spring Websocket
* Kotlin
* Gradle
* MySQL
* JWT
* JUnit 5
* Mockito
* Docker
* Kubernetes
* Github Actions
* AWS ECR
* AWS EKS
* Terraform

## Getting started


**AWS 리소스 설정을 별도의 테라폼 레포지토리로 관리합니다.**

먼저, GitHub에서 다음과 같은 과정을 거쳐 시크릿(Secrets)을 생성합니다:

1. GitHub의 저장소로 이동한 후, 'Settings' 메뉴를 선택합니다.
2. 'Secrets' 옵션을 클릭합니다.
3. 'New repository secret' 버튼을 누릅니다.

그런 다음, 아래와 같은 키-값 쌍을 등록합니다:

* AWS_ACCESS_KEY_ID: AWS 접근 키 ID
* AWS_SECRET_ACCESS_KEY: AWS 비밀 접근 키
* DB_PASSWORD: 데이터베이스 비밀번호
* DB_URL: 데이터베이스 URL
* DB_USERNAME: 데이터베이스 사용자 이름

KUBECONFIG는 아래 설정 파일을 base64 인코딩한 값으로 저장합니다. 인코딩 방법은 다음과 같습니다:

1. 터미널에서 다음 명령어를 실행하여 설정 파일을 base64로 인코딩합니다:
```bash
$ base64 -i ~/.kube/config
```
2. 출력된 base64 인코딩 값을 복사합니다.
3. GitHub Secrets에 새로운 키 'KUBECONFIG'를 생성하고, 복사한 값을 값으로 저장합니다.

# Package Structure

```bash
main/kotlin/app.klock.api
├── domain
│   └── entity
├── exception
├── functional
│   ├── account
│   ├── auth
│   ├── dDayEvent
│   ├── friemdRelation
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

# Functional
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

## 친구
1. [x] 친구 요청
2. [x] 친구 관계 확인

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
1. RSocket or GRPC
2. 리액티브 프로그래밍 스타일을 가독성을 위해 코루틴 스파일로 변경
