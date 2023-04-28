import app.klock.api.domain.entity.SocialLogin
import app.klock.api.domain.entity.SocialProvider
import app.klock.api.domain.entity.User
import app.klock.api.domain.entity.UserRole
import app.klock.api.functional.auth.dto.SocialLoginRequest
import app.klock.api.repository.SocialLoginRepository
import app.klock.api.repository.UserRepository
import app.klock.api.service.AuthService
import app.klock.api.utils.JwtUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class AuthServiceTest {

  @Mock
  private lateinit var jwtUtils: JwtUtils

  @Mock
  private lateinit var passwordEncoder: PasswordEncoder

  @Mock
  private lateinit var userRepository: UserRepository

  @Mock
  private lateinit var socialLoginRepository: SocialLoginRepository

  private lateinit var authService: AuthService

  @BeforeEach
  fun setUp() {
    authService = AuthService(jwtUtils, passwordEncoder, userRepository, socialLoginRepository)
  }

  @Test
  fun `사용자 등록`() {
    val savedUser = User(
      id = 1L,
      username = "testuser",
      email = "test@example.com",
      hashedPassword = "encoded_password",
      role = UserRole.USER,
      active = true,
      totalStudyTime = 0,
      userLevelId = 1,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now()
    )

    val socialLogin = SocialLogin(
      id = 1L,
      userId = savedUser.id!!,
      provider = SocialProvider.APPLE,
      providerUserId = "1234"
    )

    // User 생성 관련 mock
    Mockito.`when`(passwordEncoder.encode(savedUser.hashedPassword)).thenReturn("encoded_password")
    Mockito.`when`(userRepository.save(any(User::class.java))).thenReturn(Mono.just(savedUser))

    // SocialLogin 생성 관련 mock
    Mockito.`when`(socialLoginRepository.save(any(SocialLogin::class.java))).thenReturn(Mono.just(socialLogin))

    // User 생성 테스트
    StepVerifier.create(authService.signup(
      username = savedUser.username,
      email = savedUser.email,
      password = savedUser.hashedPassword))
      .expectNext(savedUser)
      .verifyComplete()

    // SocialLogin 생성 테스트
    StepVerifier.create(authService.createSocialLogin(
      userId = savedUser.id!!,
      provider = SocialProvider.APPLE,
      providerUserId = "1234"))
      .expectNext(socialLogin)
      .verifyComplete()
  }


  @Test
  fun `애플 인증`() {
    val socialLoginRequest = SocialLoginRequest(accessToken = "apple_access_token")
    val jwtToken = "jwt_token"
    val userEmail = "apple@example.com"
    val user = User(
      username = "testuser",
      email = "apple@example.com",
      hashedPassword = "password",
      role = UserRole.USER,
      active = true,
      totalStudyTime = 0,
      userLevelId = 1,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now()
    )

    Mockito.`when`(jwtUtils.getUserIdFromToken(anyString())).thenReturn(userEmail)
    Mockito.`when`(jwtUtils.generateToken(anyString(), anyList())).thenReturn(jwtToken)
    Mockito.`when`(userRepository.findByEmail(anyString())).thenReturn(Mono.empty())
    Mockito.`when`(userRepository.save(any(User::class.java))).thenReturn(Mono.just(user))

    StepVerifier.create(authService.authenticateApple(Mono.just(socialLoginRequest)))
      .expectNext(jwtToken)
      .verifyComplete()
  }

  @Test
  fun `토큰 새로 고침`() {
    val refreshToken = "refresh_token"
    val jwtToken = "jwt_token"

    Mockito.`when`(jwtUtils.validateTokenAndGetUserId(anyString())).thenReturn(jwtToken)
    Mockito.`when`(jwtUtils.generateToken(anyString(), anyList())).thenReturn(jwtToken)

    StepVerifier.create(authService.refreshToken(refreshToken))
      .expectNext(jwtToken)
      .verifyComplete()
  }
}
