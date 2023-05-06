import app.klock.api.domain.entity.SocialLogin
import app.klock.api.domain.entity.SocialProvider
import app.klock.api.domain.entity.User
import app.klock.api.domain.entity.UserRole
import app.klock.api.repository.SocialLoginRepository
import app.klock.api.repository.UserRepository
import app.klock.api.service.AuthService
import app.klock.api.utils.JwtUtils
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDateTime

class AuthServiceTest {

  private lateinit var jwtUtils: JwtUtils
  private lateinit var passwordEncoder: PasswordEncoder
  private lateinit var userRepository: UserRepository
  private lateinit var socialLoginRepository: SocialLoginRepository
  private lateinit var authService: AuthService

  @BeforeEach
  fun setUp() {
    jwtUtils = mockk<JwtUtils>()
    passwordEncoder = mockk<BCryptPasswordEncoder>()
    userRepository = mockk<UserRepository>()
    socialLoginRepository = mockk<SocialLoginRepository>()

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
    every { passwordEncoder.encode(savedUser.hashedPassword) } returns "encoded_password"
    every { userRepository.save(any()) } returns Mono.just(savedUser)

    // SocialLogin 생성 관련 mock
    every { socialLoginRepository.save(any()) } returns Mono.just(socialLogin)

    // User 생성 테스트
    StepVerifier.create(
      authService.signup(
        username = savedUser.username,
        email = savedUser.email,
        password = savedUser.hashedPassword
      )
    )
      .expectNext(savedUser)
      .verifyComplete()

    // SocialLogin 생성 테스트
    StepVerifier.create(
      authService.createSocialLogin(
        userId = savedUser.id!!,
        provider = SocialProvider.APPLE,
        providerUserId = "1234"
      )
    )
      .expectNext(socialLogin)
      .verifyComplete()
  }

  @Test
  fun `토큰 새로 고침`() {
    val refreshToken = "refresh_token"
    val jwtToken = "jwt_token"
    val userId = "1"
    val user = User(
      id = 1,
      email = "user@example.com",
      username = "testuser",
      hashedPassword = "password",
      role = UserRole.USER,
      active = true,
      totalStudyTime = 0,
      userLevelId = 1,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now()
    )

    every { jwtUtils.validateTokenAndGetUserId(refreshToken) } returns userId
    every { jwtUtils.generateToken(userId, any()) } returns jwtToken
    every { userRepository.findById(userId.toLong()) } returns Mono.just(user)

    StepVerifier.create(authService.refreshToken(refreshToken))
      .expectNext(jwtToken)
      .verifyComplete()
  }

}
