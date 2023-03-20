package app.klock.api.service

import app.klock.api.domain.entity.Account
import app.klock.api.domain.entity.AccountRole
import app.klock.api.functional.auth.dto.SocialLoginRequest
import app.klock.api.repository.AccountRepository
import app.klock.api.utils.JwtUtils
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class AuthService(
    private val jwtUtils: JwtUtils,
    private val passwordEncoder: PasswordEncoder,
    private val accountRepository: AccountRepository) {

    private val facebookAppId = "your_facebook_app_id"
    private val facebookAppSecret = "your_facebook_app_secret"
    private val appleClientId = "your_apple_client_id"

    fun create(username: String, email: String, password: String? = null): Mono<Account> {
        val account = Account(
            username = username,
            email = email,
            hashedPassword = passwordEncoder.encode(password),
            role = AccountRole.USER,
            active = true,
            totalStudyTime = 0,
            accountLevelId = 1,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        return accountRepository.save(account)
    }

    fun authenticateFacebook(socialLoginRequest: Mono<SocialLoginRequest>): Mono<String> {
        return socialLoginRequest.flatMap { request ->
            // Facebook 액세스 토큰을 사용하여 사용자 정보를 가져옵니다.
            val facebookAccessToken = request.accessToken
            val userInfoUrl = "https://graph.facebook.com/me?fields=id,email,name&access_token=$facebookAccessToken"
            WebClient.create().get().uri(userInfoUrl).retrieve().bodyToMono(JsonNode::class.java)
                .flatMap { userInfo ->
                    val userId = userInfo.get("id").asText()
                    val email = userInfo.get("email").asText()
                    val name = userInfo.get("name").asText()

                    // 사용자를 인증하고 JWT 토큰을 생성합니다.
                    authenticateAndGenerateJwt(userId, email, name)
                }
        }
    }

    fun authenticateApple(socialLoginRequest: Mono<SocialLoginRequest>): Mono<String> {
        return socialLoginRequest.flatMap { request ->
            val appleAccessToken = request.accessToken
            // Apple 액세스 토큰을 사용하여 사용자 정보를 가져옵니다.
            // 여기서는 구현의 간소화를 위해 사용자 ID만 사용합니다.
            val appleUserId = jwtUtils.getUsernameFromToken(appleAccessToken)

            // 사용자를 인증하고 JWT 토큰을 생성합니다.
            authenticateAndGenerateJwt(appleUserId, "apple@example.com", "Apple User")
        }
    }

    private fun authenticateAndGenerateJwt(userId: String, email: String, name: String): Mono<String> {
        return accountRepository.findByEmail(email)
            .switchIfEmpty(Mono.defer {
                val newUser = Account(
                    username = name,
                    email = email,
                    role = AccountRole.USER,
                    active = true,
                    totalStudyTime = 0,
                    accountLevelId = 1,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now())
                accountRepository.save(newUser)
            })
            .map { user ->
                // JWT 토큰을 생성합니다.
                jwtUtils.generateToken(user.email)
            }
    }

    fun refreshToken(refreshToken: String): Mono<String> {
        return Mono.fromCallable {
            jwtUtils.validateTokenAndGetEmail(refreshToken)
        }.flatMap { email ->
            val newToken = jwtUtils.generateToken(email)
            Mono.just(newToken)
        }
    }

}
