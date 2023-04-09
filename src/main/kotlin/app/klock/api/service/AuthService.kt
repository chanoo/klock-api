package app.klock.api.service

import app.klock.api.domain.entity.Account
import app.klock.api.domain.entity.AccountRole
import app.klock.api.domain.entity.SocialLogin
import app.klock.api.domain.entity.SocialProvider
import app.klock.api.functional.auth.dto.SocialLoginRequest
import app.klock.api.repository.AccountRepository
import app.klock.api.repository.SocialLoginRepository
import app.klock.api.utils.JwtUtils
import com.fasterxml.jackson.databind.JsonNode
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
  private val accountRepository: AccountRepository,
  private var socialLoginRepository: SocialLoginRepository) {

  private val facebookAppId = "your_facebook_app_id"
  private val facebookAppSecret = "your_facebook_app_secret"
  private val appleClientId = "your_apple_client_id"


  fun createAccount(username: String,
                    email: String? = null,
                    password: String? = null): Mono<Account> {
    val account = Account(
      username = username,
      email = email,
      hashedPassword = password?.let { passwordEncoder.encode(it) },
      role = AccountRole.USER,
      active = true,
      totalStudyTime = 0,
      accountLevelId = 1,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now()
    )

    return accountRepository.save(account)
  }

  fun createSocialLogin(accountId: Long,
                        provider: SocialProvider,
                        providerUserId: String): Mono<SocialLogin> {
    val socialLogin = SocialLogin(
      accountId = accountId,
      provider = provider,
      providerUserId = providerUserId
    )

    return socialLoginRepository.save(socialLogin)
  }

  private fun authenticateSocial(socialProvider: SocialProvider, providerUserId: String): Mono<String> {
    return socialLoginRepository.findByProviderAndProviderUserId(socialProvider, providerUserId)
      .flatMap { socialLogin ->
        accountRepository.findById(socialLogin.accountId)
          .flatMap { account ->
            // JWT 토큰을 생성합니다.
            Mono.just(jwtUtils.generateToken(account.id.toString()))
          }
      }
  }

  fun authenticateFacebook(socialLoginRequest: Mono<SocialLoginRequest>): Mono<String> {
    return socialLoginRequest.flatMap { request ->
      // Facebook 액세스 토큰을 사용하여 사용자 정보를 가져옵니다.
      val facebookAccessToken = request.accessToken
      val userInfoUrl = "https://graph.facebook.com/me?fields=id,email,name&access_token=$facebookAccessToken"
      WebClient.create().get().uri(userInfoUrl).retrieve().bodyToMono(JsonNode::class.java)
        .flatMap { userInfo ->
          val userId = userInfo.get("id").asText()

          // accountId로 Account가 있는지 확인해서 가져와서 JWT 토큰을 생성하고 반환 한다.
          authenticateSocial(SocialProvider.FACEBOOK, userId)
        }
    }
  }

  fun authenticateApple(socialLoginRequest: Mono<SocialLoginRequest>): Mono<String> {
    return socialLoginRequest.flatMap { request ->
      val appleAccessToken = request.accessToken
      // Apple 공개 키를 가져옵니다.
      fetchApplePublicKeys().flatMap { jwkSet ->
        // Apple 액세스 토큰 (JWT)를 검증합니다.
        validateAppleJwt(appleAccessToken, jwkSet)
      }.flatMap { jwtClaimsSet ->
        // JWT에서 사용자 ID를 가져옵니다.
        val appleUserId = jwtClaimsSet.subject

        // accountId로 Account가 있는지 확인해서 가져와서 JWT 토큰을 생성하고 반환한다.
        authenticateSocial(SocialProvider.APPLE, appleUserId)
      }
    }
  }

  fun refreshToken(refreshToken: String): Mono<String> {
    return Mono.fromCallable {
      jwtUtils.validateTokenAndGetUserId(refreshToken)
    }.flatMap { userId ->
      val newToken = jwtUtils.generateToken(userId)
      Mono.just(newToken)
    }
  }

  private fun fetchApplePublicKeys(): Mono<ImmutableJWKSet<SecurityContext>> {
    val applePublicKeysUrl = "https://appleid.apple.com/auth/keys"
    return WebClient.create()
      .get()
      .uri(applePublicKeysUrl)
      .retrieve()
      .bodyToMono(String::class.java)
      .map { jwkSetString ->
        val parsedJWKSet = JWKSet.parse(jwkSetString)
        ImmutableJWKSet<SecurityContext>(parsedJWKSet)
      }
  }

  private fun validateAppleJwt(token: String, jwkSet: ImmutableJWKSet<SecurityContext>): Mono<JWTClaimsSet> {
    val jwtProcessor: ConfigurableJWTProcessor<SecurityContext> = DefaultJWTProcessor()
    val jwsKeySelector = JWSVerificationKeySelector<SecurityContext>(JWSAlgorithm.RS256, jwkSet)
    jwtProcessor.jwsKeySelector = jwsKeySelector

    return try {
      val claimsSet = jwtProcessor.process(token, null) // null을 사용하여 SecurityContext를 생략합니다.
      Mono.just(claimsSet)
    } catch (e: Exception) {
      Mono.error(e)
    }
  }

}
