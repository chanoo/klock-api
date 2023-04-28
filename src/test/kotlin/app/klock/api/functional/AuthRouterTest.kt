package app.klock.api.functional

import app.klock.api.config.TestConfig
import app.klock.api.domain.entity.*
import app.klock.api.functional.auth.dto.LoginRequest
import app.klock.api.functional.auth.dto.SignUpReqDTO
import app.klock.api.service.AuthService
import app.klock.api.service.UserService
import app.klock.api.service.UserTagService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [TestConfig::class])
@ActiveProfiles("test")
class AuthRouterTest @Autowired constructor(
  private val client: WebTestClient
) {
  @MockBean
  private lateinit var userService: UserService

  @MockBean
  private lateinit var authService: AuthService

  @MockBean
  private lateinit var userTagService: UserTagService

  // 테스트 데이터 설정
  lateinit var user: User
  lateinit var newUser: User

  @BeforeEach
  fun setUp() {
    // 테스트에 사용할 사용자 데이터를 설정합니다.
    user = User(
      username = "user1",
      email = "user1@example.com",
      totalStudyTime = 0,
      userLevelId = 1,
      role = UserRole.USER,
      active = true,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now())

    newUser = User(
      id = 1L,
      username = "user1",
      email = "user1@example.com",
      hashedPassword = "password",
      totalStudyTime = 0,
      userLevelId = 1,
      role = UserRole.USER,
      active = true,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now())
  }

  @Test
  fun `회원 가입`() {
    // Prepare test data
    val userToSave = User(
      username = "user1",
      email = "user1@example.com",
      hashedPassword = "encoded_test_password",
      role = UserRole.USER,
      active = true,
      totalStudyTime = 0,
      userLevelId = 1,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now()
    )
    val savedAccount = userToSave.copy(id = 1L)

    var socialLoginToSave = SocialLogin(
      provider = SocialProvider.APPLE,
      providerUserId = "1234",
      userId = savedAccount.id!!,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now())

    val savedSocialLogin = socialLoginToSave.copy(id = 1L)

    val userTagToSave = UserTag(
      userId = savedAccount.id!!,
      tagId = 1L
    )

    val savedAccountTag = userTagToSave.copy(id = 1L)

    // Save user mock
    Mockito.`when`(authService.signup(
      username = userToSave.username,
      email = userToSave.email,
      password = userToSave.hashedPassword)).thenReturn(Mono.just(savedAccount))

    Mockito.`when`(authService.createSocialLogin(
      userId = savedAccount.id!!,
      provider = SocialProvider.APPLE,
      providerUserId = "1234")).thenReturn(Mono.just(savedSocialLogin))

    Mockito.`when`(userTagService.create(userTagToSave)).thenReturn(Mono.just(savedAccountTag))

    // 요청 테스트
    val signUpReqDTO = SignUpReqDTO(
      username = userToSave.username,
      provider = SocialProvider.APPLE,
      providerUserId = "1234",
      email = userToSave.email,
      password = userToSave.hashedPassword,
      tagId = 1L
    )

    // Make a request to create a user
    client.post().uri("/api/auth/signup")
      .contentType(MediaType.APPLICATION_JSON)
      .body(BodyInserters.fromValue(signUpReqDTO))
      .exchange()
      .expectStatus().isCreated
      .expectBody()
      .jsonPath("$.username").isEqualTo(savedAccount.username)
      .jsonPath("$.provider").isEqualTo(savedSocialLogin.provider.toString())
      .jsonPath("$.providerUserId").isEqualTo(savedSocialLogin.providerUserId)
  }

  @Test
  fun `로그인 성공`() {
    val loginRequest = LoginRequest(
      email = "user1@example.com",
      password = "password"
    )

    Mockito.`when`(userService.findByEmail(loginRequest.email)).thenReturn(Mono.just(user))
    Mockito.`when`(userService.validatePassword(loginRequest.password, user.hashedPassword)).thenReturn(true)

    client.post().uri("/api/auth/signin")
      .contentType(MediaType.APPLICATION_JSON)
      .body(BodyInserters.fromValue(loginRequest))
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.token").isNotEmpty
  }

  @Test
  fun `로그인 실패 - 잘못된 이메일`() {
    val loginRequest = LoginRequest(
      email = "wrong@example.com",
      password = "password"
    )

    Mockito.`when`(userService.findByEmail(loginRequest.email)).thenReturn(Mono.empty())

    client.post().uri("/api/auth/signin")
      .contentType(MediaType.APPLICATION_JSON)
      .body(BodyInserters.fromValue(loginRequest))
      .exchange()
      .expectStatus().isBadRequest
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.error").isEqualTo("Invalid username or password")
  }

  @Test
  fun `로그인 실패 - 잘못된 비밀번호`() {
    val loginRequest = LoginRequest(
      email = "user1@example.com",
      password = "wrongpassword"
    )

    Mockito.`when`(userService.findByEmail(loginRequest.email)).thenReturn(Mono.just(user))
    Mockito.`when`(userService.validatePassword(loginRequest.password, user.hashedPassword)).thenReturn(false)

    client.post().uri("/api/auth/signin")
      .contentType(MediaType.APPLICATION_JSON)
      .body(BodyInserters.fromValue(loginRequest))
      .exchange()
      .expectStatus().isBadRequest
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.error").isEqualTo("Invalid username or password")
  }

  @Test
  fun `토큰 갱신`() {
    val refreshToken = "valid_refresh_token"
    val newAccessToken = "new_access_token"

    Mockito.`when`(authService.refreshToken(refreshToken)).thenReturn(Mono.just(newAccessToken))

    client.post().uri("/api/auth/refresh-token")
      .contentType(MediaType.APPLICATION_JSON)
      .body(BodyInserters.fromValue(mapOf("refreshToken" to refreshToken)))
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.token").isEqualTo(newAccessToken)
  }

}
