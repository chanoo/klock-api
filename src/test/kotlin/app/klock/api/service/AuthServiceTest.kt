import app.klock.api.domain.entity.Account
import app.klock.api.domain.entity.AccountRole
import app.klock.api.domain.entity.SocialLogin
import app.klock.api.domain.entity.SocialProvider
import app.klock.api.functional.auth.dto.SocialLoginRequest
import app.klock.api.repository.AccountRepository
import app.klock.api.repository.SocialLoginRepository
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
  private lateinit var accountRepository: AccountRepository

  @Mock
  private lateinit var socialLoginRepository: SocialLoginRepository

  private lateinit var authService: AuthService

  @BeforeEach
  fun setUp() {
    authService = AuthService(jwtUtils, passwordEncoder, accountRepository, socialLoginRepository)
  }

  @Test
  fun `사용자 등록`() {
    val savedAccount = Account(
      id = 1L,
      username = "testuser",
      email = "test@example.com",
      hashedPassword = "encoded_password",
      role = AccountRole.USER,
      active = true,
      totalStudyTime = 0,
      accountLevelId = 1,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now()
    )

    val socialLogin = SocialLogin(
      id = 1L,
      accountId = savedAccount.id!!,
      provider = SocialProvider.APPLE,
      providerUserId = "1234"
    )

    // Account 생성 관련 mock
    Mockito.`when`(passwordEncoder.encode(savedAccount.hashedPassword)).thenReturn("encoded_password")
    Mockito.`when`(accountRepository.save(any(Account::class.java))).thenReturn(Mono.just(savedAccount))

    // SocialLogin 생성 관련 mock
    Mockito.`when`(socialLoginRepository.save(any(SocialLogin::class.java))).thenReturn(Mono.just(socialLogin))

    // Account 생성 테스트
    StepVerifier.create(authService.createAccount(
      username = savedAccount.username,
      email = savedAccount.email,
      password = savedAccount.hashedPassword))
      .expectNext(savedAccount)
      .verifyComplete()

    // SocialLogin 생성 테스트
    StepVerifier.create(authService.createSocialLogin(
      accountId = savedAccount.id!!,
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
    val account = Account(
      username = "testuser",
      email = "apple@example.com",
      hashedPassword = "password",
      role = AccountRole.USER,
      active = true,
      totalStudyTime = 0,
      accountLevelId = 1,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now()
    )

    Mockito.`when`(jwtUtils.getUserIdFromToken(anyString())).thenReturn(userEmail)
    Mockito.`when`(jwtUtils.generateToken(anyString(), anyList())).thenReturn(jwtToken)
    Mockito.`when`(accountRepository.findByEmail(anyString())).thenReturn(Mono.empty())
    Mockito.`when`(accountRepository.save(any(Account::class.java))).thenReturn(Mono.just(account))

    StepVerifier.create(authService.authenticateApple(Mono.just(socialLoginRequest)))
      .expectNext(jwtToken)
      .verifyComplete()
  }

  @Test
  fun `토큰 새로 고침`() {
    val refreshToken = "refresh_token"
    val jwtToken = "jwt_token"
    val userId = "56"

    Mockito.`when`(jwtUtils.validateTokenAndGetUserId(anyString())).thenReturn(jwtToken)
    Mockito.`when`(jwtUtils.generateToken(anyString(), anyList())).thenReturn(jwtToken)

    StepVerifier.create(authService.refreshToken(refreshToken))
      .expectNext(jwtToken)
      .verifyComplete()
  }
}
