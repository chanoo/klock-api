package app.klock.api.functional.auth.handler

import app.klock.api.domain.entity.Account
import app.klock.api.domain.entity.AccountRole
import app.klock.api.functional.auth.dto.CreateUserRequest
import app.klock.api.functional.auth.dto.LoginRequest
import app.klock.api.service.AccountService
import app.klock.api.service.AuthService
import app.klock.api.utils.JwtUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@WebFluxTest(controllers = [AuthHandler::class])
@ActiveProfiles("test")
class AuthHandlerTest @Autowired constructor(
    private val client: WebTestClient
) {
    @MockBean
    private lateinit var accountService: AccountService

    @MockBean
    private lateinit var authService: AuthService

    @MockBean
    private lateinit var jwtUtils: JwtUtils

    private lateinit var authHandler: AuthHandler

    @BeforeEach
    fun setUp() {
        authHandler = AuthHandler(accountService, authService, jwtUtils)
    }

    // 테스트 메소드 작성
    // 회원가입 테스트
    @Test
    fun `test signup`() {
        val testRequest = CreateUserRequest(
            name = "testuser",
            email = "test@example.com",
            password = "password123"
        )

        val testAccount = Account(
            id = 1,
            username = "testuser",
            email = "test@example.com",
            hashedPassword = "hashedpassword",
            role = AccountRole.USER,
            active = true,
            totalStudyTime = 0,
            accountLevelId = 1,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        Mockito.`when`(authService.registerUser(testAccount)).thenReturn(Mono.just(testAccount))

        client.post()
            .uri("/api/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(testRequest)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("$.id").value<Long> { assertThat(it).isEqualTo(testAccount.id) }
            .jsonPath("$.name").value<String> { assertThat(it).isEqualTo(testAccount.username) }
            .jsonPath("$.email").value<String> { assertThat(it).isEqualTo(testAccount.email) }

        Mockito.verify(authService, Mockito.times(1)).registerUser(Mockito.any(Account::class.java))
    }



    // 로그인 테스트
    @Test
    fun `test login`() {
        val loginRequest = LoginRequest("test@example.com", "password123")
        val token = "test_token"

        Mockito.`when`(accountService.findByEmail(Mockito.anyString())).thenReturn(Mono.just(Account(
            id = 1,
            username = "testuser",
            email = "test@example.com",
            hashedPassword = "hashedpassword",
            role = AccountRole.USER,
            active = true,
            totalStudyTime = 0,
            accountLevelId = 1,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )))

        Mockito.`when`(accountService.validatePassword(Mockito.anyString(), Mockito.anyString())).thenReturn(true)

        Mockito.`when`(jwtUtils.generateToken(Mockito.anyString())).thenReturn(token)

        client.post()
            .uri("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(loginRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.token").isEqualTo(token)
    }

}
