package app.klock.api.functional

import app.klock.api.config.TestConfig
import app.klock.api.domain.entity.Account
import app.klock.api.domain.entity.AccountRole
import app.klock.api.functional.auth.dto.CreateUserRequest
import app.klock.api.functional.auth.dto.LoginRequest
import app.klock.api.service.AccountService
import app.klock.api.service.AuthService
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
    private lateinit var accountService: AccountService
    @MockBean
    private lateinit var authService: AuthService

    // 테스트 데이터 설정
    lateinit var account: Account
    lateinit var newAccount: Account

    @BeforeEach
    fun setUp() {
        // 테스트에 사용할 사용자 데이터를 설정합니다.
        account = Account(
            username = "user1",
            email = "user1@example.com",
            totalStudyTime = 0,
            accountLevelId = 1,
            role = AccountRole.USER,
            active = true,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now())

        newAccount = Account(
            id = 1L,
            username = "user1",
            email = "user1@example.com",
            hashedPassword = "password",
            totalStudyTime = 0,
            accountLevelId = 1,
            role = AccountRole.USER,
            active = true,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now())
    }

    @Test
    fun `회원 가입`() {
        // Save user mock
        Mockito.`when`(accountService.save(account)).thenReturn(Mono.just(newAccount))

        // 요청 테스트
        val createUserRequest = CreateUserRequest(
            name = "user10",
            email = "user10@example.com")

        // Make a request to create a user
        client.post().uri("/api/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(createUserRequest))
            .exchange()
            .expectStatus().isCreated
    }

    @Test
    fun `로그인 성공`() {
        val loginRequest = LoginRequest(
            email = "user1@example.com",
            password = "password"
        )

        Mockito.`when`(accountService.findByEmail(loginRequest.email)).thenReturn(Mono.just(account))
        Mockito.`when`(accountService.validatePassword(loginRequest.password, account.hashedPassword)).thenReturn(true)

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

        Mockito.`when`(accountService.findByEmail(loginRequest.email)).thenReturn(Mono.empty())

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

        Mockito.`when`(accountService.findByEmail(loginRequest.email)).thenReturn(Mono.just(account))
        Mockito.`when`(accountService.validatePassword(loginRequest.password, account.hashedPassword)).thenReturn(false)

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
