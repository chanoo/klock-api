# Rest API 설계 원칙 표준화

REST(REpresentational State Transfer)는 웹 서비스를 구축하기 위한 아키텍처 스타일로, 웹의 기본 구조와 잘 어울리며 확장성과 유지 관리의 편리성을 제공합니다. RESTful API 설계 시 고려해야 할 원칙은 다음과 같습니다:

1. 자원 지향적 설계(Resource Oriented Design): REST API는 자원(엔티티)에 초점을 맞추며, 각 자원은 고유한 URI(Uniform Resource Identifier)를 갖습니다. URI는 명사를 사용해 설계하며 동사는 사용하지 않습니다.

2. 표준 HTTP 메서드 사용: 자원에 대한 CRUD(Create, Read, Update, Delete) 작업을 수행하기 위해 표준 HTTP 메서드를 사용합니다.
- POST: 새로운 자원 생성
- GET: 자원 읽기
- PUT 또는 PATCH: 자원 업데이트
- DELETE: 자원 삭제

4. 명확한 계층 구조: API의 리소스들은 명확한 계층 구조를 따라야 합니다. 예를 들어, /users/{userId}/orders/{orderId}와 같이 사용자와 주문 간의 관계를 표현할 수 있습니다.

5. 상태 코드를 통한 응답: REST API는 클라이언트에게 명확한 상태 코드를 반환해야 합니다. HTTP 상태 코드(예: 200 OK, 201 Created, 400 Bad Request, 404 Not Found 등)를 사용하여 작업의 성공, 실패, 오류 등을 전달합니다.

6. HATEOAS(Hypermedia as the Engine of Application State): 클라이언트가 애플리케이션 상태를 이해하고 자원 간의 관계를 파악할 수 있도록 응답에 하이퍼미디어 링크를 포함합니다. 이를 통해 클라이언트는 API의 구조를 쉽게 탐색할 수 있습니다.

7. 캐싱 지원: 캐싱을 통해 성능과 확장성을 향상시킬 수 있습니다. Last-Modified, ETag 및 Cache-Control 헤더를 사용하여 클라이언트와 서버 간 캐싱을 지원합니다.

8. 필터링, 정렬 및 페이지네이션: 목록이나 컬렉션을 반환하는 API에서는 필터링, 정렬 및 페이지네이션을 제공하여 클라이언트가 원하는 데이터를 쉽게 가져올 수 있도록 합니다.

9. 버전 관리: API의 버전을 관리하여 변경 사항이 클라이언트에 영향을 미치지 않도록 합니다. 버전을 명시적으로 표시하려면 URL에 버전 정보를 포함시키거나 헤더를 사용할 수 있습니다. 예를 들어, /v1/users 또는 Accept: application/vnd.example.v1+json과 같은 방식으로 표현할 수 있습니다.

10. 보안: RESTful API의 보안을 위해 인증 및 권한 관리를 구현해야 합니다. 인증을 위해 일반적으로 OAuth, JWT(JSON Web Token), API 키 등의 방식을 사용하며, 권한 관리를 위해 RBAC(Role-Based Access Control) 같은 접근 제어 메커니즘을 사용할 수 있습니다.

11. 명확하고 자세한 문서화: API의 사용자가 쉽게 이해하고 사용할 수 있도록 명확하고 자세한 문서를 제공해야 합니다. 문서에는 엔드포인트, HTTP 메서드, 요청 및 응답 헤더, 요청 및 응답 본문, 에러 코드 등에 대한 정보가 포함되어야 하며, OpenAPI(Swagger)나 RAML과 같은 형식을 사용해 문서를 작성할 수 있습니다.

이러한 원칙들을 따르면 RESTful API 설계에 있어 일관성과 확장성을 높일 수 있으며, 사용자가 API를 쉽게 이해하고 사용할 수 있는 구조를 만들 수 있습니다. 이 원칙들은 모든 상황에 적용되지는 않지만, 일반적인 RESTful API 설계 가이드라인으로 사용됩니다.

# 설명한걸 바탕으로 다양한 예를 들어 설명 해줄래요?

네, 앞서 언급한 REST API 설계 원칙들을 바탕으로 다양한 예시를 들어 설명드리겠습니다.

**예시 1: 온라인 쇼핑몰 API**

1. 사용자 조회
- GET /users/{userId}
- 사용자 정보를 조회합니다.

2. 상품 조회
- GET /products/{productId}
- 특정 상품 정보를 조회합니다.

3. 상품 검색
- GET /products?search={searchTerm}&sort={sortBy}&order={order}&page={page}&size={size}
- 상품을 검색하고, 정렬하며, 페이지네이션을 적용하여 결과를 반환합니다.

4. 주문 생성
- POST /users/{userId}/orders
- 사용자의 주문을 생성합니다.

5. 주문 조회
- GET /users/{userId}/orders/{orderId}
- 사용자의 특정 주문을 조회합니다.

**예시 2: 블로그 API**

1. 사용자의 글 작성
- POST /users/{userId}/posts
- 사용자가 새로운 글을 작성합니다.

2. 사용자의 글 목록 조회
- GET /users/{userId}/posts?tag={tag}&sort={sortBy}&order={order}&page={page}&size={size}
- 사용자의 글 목록을 조회하고, 태그로 필터링하며, 정렬하고, 페이지네이션을 적용하여 결과를 반환합니다.

3. 글 조회
- GET /posts/{postId}
- 특정 글을 조회합니다.

4. 글 수정
- PUT /users/{userId}/posts/{postId}
- 사용자가 글을 수정합니다.

5. 글 삭제
- DELETE /users/{userId}/posts/{postId}
- 사용자가 글을 삭제합니다.
