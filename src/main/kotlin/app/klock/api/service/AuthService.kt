package app.klock.api.service

import app.klock.api.domain.entity.SocialLogin
import app.klock.api.domain.entity.SocialProvider
import app.klock.api.domain.entity.User
import app.klock.api.domain.entity.UserRole
import app.klock.api.functional.auth.AppleLoginRequest
import app.klock.api.functional.auth.LoginDto
import app.klock.api.functional.auth.SocialLoginRequest
import app.klock.api.repository.SocialLoginRepository
import app.klock.api.repository.UserRepository
import app.klock.api.utils.CryptoUtils
import app.klock.api.utils.JwtUtils
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.LocalDateTime


@Service
class AuthService(
  private val jwtUtils: JwtUtils,
  private val passwordEncoder: PasswordEncoder,
  private val userRepository: UserRepository,
  private var socialLoginRepository: SocialLoginRepository,
  private val cryptoUtils: CryptoUtils
) {

  private val facebookAppId = "your_facebook_app_id"
  private val facebookAppSecret = "your_facebook_app_secret"
  private val appleClientId = "your_apple_client_id"


  fun signup(
    nickname: String,
    email: String? = null,
    password: String? = null
  ): Mono<User> {
    val user = User(
      nickname = nickname,
      email = email,
      hashedPassword = password?.let { passwordEncoder.encode(it) },
      role = UserRole.USER,
      active = true,
      totalStudyTime = 0,
      userLevelId = 1,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now(),
      profileImage = null
    )

    return userRepository.save(user)
  }

  fun createSocialLogin(
    userId: Long,
    provider: SocialProvider,
    providerUserId: String
  ): Mono<SocialLogin> {
    val socialLogin = SocialLogin(
      userId = userId,
      provider = provider,
      providerUserId = providerUserId
    )

    return socialLoginRepository.save(socialLogin)
  }

  private fun authenticateSocial(socialProvider: SocialProvider, providerUserId: String): Mono<LoginDto> {
    return socialLoginRepository.findByProviderAndProviderUserId(socialProvider, providerUserId)
      .flatMap { socialLogin ->
        userRepository.findById(socialLogin.userId)
          .flatMap { user ->
            // JWT 토큰을 생성합니다.
            Mono.just(
              LoginDto(
                token = jwtUtils.generateToken(user.id.toString(), listOf(user.role.name)),
                userId = user.id,
                publicKey = cryptoUtils.getPublicKey()
              )
            )
          }
      }
  }

  fun authenticateSocial(socialLoginRequest: Mono<SocialLoginRequest>): Mono<LoginDto> {
    return socialLoginRequest.flatMap { request ->
      val provider = request.provider
      val providerUserId = request.providerUserId
      authenticateSocial(provider, providerUserId)
    }.switchIfEmpty(Mono.error(NoSuchElementException("Authentication failed")))
  }

  fun authenticateApple(appleLoginRequest: Mono<AppleLoginRequest>): Mono<LoginDto> {
    return appleLoginRequest.flatMap { request ->
      val appleAccessToken = request.accessToken
      // Apple 공개 키를 가져옵니다.
      fetchApplePublicKeys().flatMap { jwkSet ->
        // Apple 액세스 토큰 (JWT)를 검증합니다.
        validateAppleJwt(appleAccessToken, jwkSet)
      }.flatMap { jwtClaimsSet ->
        // JWT에서 사용자 ID를 가져옵니다.
        val appleUserId = jwtClaimsSet.subject

        // userId로 User가 있는지 확인해서 가져와서 JWT 토큰을 생성하고 반환한다.
        authenticateSocial(SocialProvider.APPLE, appleUserId)
      }
    }.switchIfEmpty(Mono.error(NoSuchElementException("Authentication failed")))
  }

  fun refreshToken(refreshToken: String): Mono<String> {
    return Mono.fromCallable {
      jwtUtils.validateTokenAndGetUserId(refreshToken)
    }.flatMap { userId ->
      userRepository.findById(userId.toLong())
        .flatMap { user ->
          // User 역할을 가져옵니다.
          val roles = listOf(user.role.name)

          // 새 JWT 토큰을 생성하고 반환합니다.
          val newToken = jwtUtils.generateToken(userId, roles)
          Mono.just(newToken)
        }
    }
  }

  // Apple의 공개 키를 가져오는 함수입니다.
  private fun fetchApplePublicKeys(): Mono<ImmutableJWKSet<SecurityContext>> {
    // Apple의 공개 키를 가져오기 위한 URL입니다.
    val applePublicKeysUrl = "https://appleid.apple.com/auth/keys"
    return WebClient.create()
      .get()
      .uri(applePublicKeysUrl)
      .retrieve()
      .bodyToMono(String::class.java)
      .map { jwkSetString ->
        // JSON 형식의 JWKSet 문자열을 JWKSet 객체로 변환합니다.
        val parsedJWKSet = JWKSet.parse(jwkSetString)
        // 변환된 JWKSet 객체를 ImmutableJWKSet로 변환합니다.
        ImmutableJWKSet<SecurityContext>(parsedJWKSet)
      }
  }

  // 주어진 Apple JWT 토큰을 검증하는 함수입니다.
  private fun validateAppleJwt(token: String, jwkSet: ImmutableJWKSet<SecurityContext>): Mono<JWTClaimsSet> {
    // JWT 처리를 위한 ConfigurableJWTProcessor를 생성합니다.
    val jwtProcessor: ConfigurableJWTProcessor<SecurityContext> = DefaultJWTProcessor()
    // JWS 알고리즘과 JWKSet을 사용하여 JWSVerificationKeySelector를 생성합니다.
    val jwsKeySelector = JWSVerificationKeySelector<SecurityContext>(JWSAlgorithm.RS256, jwkSet)
    // 생성한 JWSVerificationKeySelector를 jwtProcessor의 jwsKeySelector로 설정합니다.
    jwtProcessor.jwsKeySelector = jwsKeySelector

    return try {
      // Apple JWT 토큰을 검증하고 ClaimsSet을 반환합니다.
      val claimsSet = jwtProcessor.process(token, null) // null을 사용하여 SecurityContext를 생략합니다.
      Mono.just(claimsSet)
    } catch (e: Exception) {
      // 검증 과정에서 오류가 발생한 경우 Mono.error로 처리합니다.
      Mono.error(e)
    }
  }


}
