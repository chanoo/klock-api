package app.klock.api.functional

import app.klock.api.domain.entity.SocialProvider
import app.klock.api.functional.auth.AuthHandler
import app.klock.api.functional.auth.AuthRouter
import app.klock.api.functional.auth.dto.LoginRequest
import app.klock.api.functional.auth.dto.LoginResponse
import app.klock.api.functional.auth.dto.SignUpReqDTO
import app.klock.api.functional.auth.dto.SignUpResDTO
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.ServerResponse

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthRouterTest {

  private lateinit var authRouter: AuthRouter
  private val authHandler = mockk<AuthHandler>()

  @BeforeEach
  fun setUp() {
    authRouter = AuthRouter(authHandler)
  }

  @Test
  fun `POST 요청으로 회원 가입 테스트`() {

    val signUpReqDTO = SignUpReqDTO(
      username = "user1",
      email = "user1@example.com",
      password = "test_password",
      providerUserId = "test_provider_user_id",
      tagId = 1,
      provider = SocialProvider.APPLE
    )

    val signUpResDTO = SignUpResDTO(
      id = 1L,
      username = "user1",
      email = "user1@example.com",
      accessToken = "valid_token",
      refreshToken = "valid_refresh_token",
      provider = SocialProvider.APPLE,
      providerUserId = "test_provider_user_id",
      tagId = 1L,
    )

    coEvery { authHandler.signup(any()) } coAnswers {
      ServerResponse.status(HttpStatus.CREATED).bodyValue(signUpResDTO)
    }

    val client = WebTestClient.bindToRouterFunction(authRouter.authRoutes()).build()

    client.post()
      .uri("/api/auth/signup")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(signUpReqDTO)
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.CREATED)
      .expectBody(SignUpResDTO::class.java)
      .value { actualSignUpResponse ->
        assertEquals(1L, actualSignUpResponse.id, "ID가 1이어야 합니다.")
        assertEquals(signUpReqDTO.username, actualSignUpResponse.username)
        assertEquals(signUpReqDTO.email, actualSignUpResponse.email)
      }
  }

  @Test
  fun `POST 요청으로 로그인 테스트`() {

    val loginRequest = LoginRequest(
      email = "user1@example.com",
      password = "test_password"
    )

    val loginResponse = LoginResponse(
      token = "valid_token",
    )

    coEvery { authHandler.signin(any()) } coAnswers {
      ServerResponse.ok().bodyValue(loginResponse)
    }

    val client = WebTestClient.bindToRouterFunction(authRouter.authRoutes()).build()

    client.post()
      .uri("/api/auth/signin")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(loginRequest)
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.OK)
      .expectBody(LoginResponse::class.java)
      .value { actualLoginResponse ->
        assertEquals(actualLoginResponse.token, loginResponse.token)
      }
  }

  @Test
  fun `토큰 갱신`() {
    val refreshToken = "valid_refresh_token"
    val newAccessToken = "new_access_token"

    coEvery { authHandler.refreshToken(any()) } coAnswers {
      ServerResponse.ok().bodyValue(mapOf("token" to newAccessToken))
    }

    val client = WebTestClient.bindToRouterFunction(authRouter.authRoutes()).build()

    client.post().uri("/api/auth/refresh-token")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(mapOf("refreshToken" to refreshToken))
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.token").isEqualTo(newAccessToken)
  }

}
