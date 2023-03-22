package app.klock.api.functional.auth

import app.klock.api.functional.auth.dto.*
import app.klock.api.service.AccountService
import app.klock.api.service.AuthService
import app.klock.api.utils.JwtUtils
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono


@Component
class AuthHandler(
    private val authService: AuthService,
    private val accountService: AccountService,
    private val jwtUtils: JwtUtils
) {

    // 회원가입 요청 처리
    fun signup(request: ServerRequest): Mono<ServerResponse> =
        request.bodyToMono(CreateUserRequest::class.java)
            .flatMap { request -> authService.create(username = request.username, email = request.email, password = request.password)
                .flatMap { user ->
                    ServerResponse.status(HttpStatus.CREATED).bodyValue(AuthDto(user.id, user.username, user.email))
                }
            }

    // 로그인 요청 처리
    fun signin(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(LoginRequest::class.java)
            .flatMap { loginRequest ->
                accountService.findByEmail(loginRequest.email)
                    .filter { user -> accountService.validatePassword(loginRequest
                        .password, user.hashedPassword) }
                    .switchIfEmpty(Mono.error(Exception("Invalid username or password")))
                    .flatMap { user ->
                        val token = jwtUtils.generateToken(user.email)
                        ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(mapOf("token" to token))
                    }
            }
            .onErrorResume { error ->
                ServerResponse.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(mapOf("error" to error.message))
            }
    }

    // 토큰 갱신
    fun refreshToken(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(RefreshTokenRequest::class.java)
            .flatMap { refreshTokenRequest ->
                authService.refreshToken(refreshTokenRequest.refreshToken)
                    .flatMap { jwt ->
                        ServerResponse.ok().bodyValue(mapOf("token" to jwt))
                    }
            }
            .onErrorResume { error ->
                ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue(mapOf("error" to error.localizedMessage))
            }
    }

    // 페이스북 로그인 요청 처리
    fun authenticateFacebook(request: ServerRequest): Mono<ServerResponse> {
        val socialLoginRequest = request.bodyToMono(SocialLoginRequest::class.java)
        return authService.authenticateFacebook(socialLoginRequest)
            .flatMap { jwt -> ServerResponse.ok().bodyValue(mapOf("token" to jwt)) }
            .onErrorResume { error -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue(mapOf("error" to error.localizedMessage)) }
    }

    // 애플 로그인 요청 처리
    fun authenticateApple(request: ServerRequest): Mono<ServerResponse> {
        val socialLoginRequest = request.bodyToMono(SocialLoginRequest::class.java)
        return authService.authenticateApple(socialLoginRequest)
            .flatMap { jwt -> ServerResponse.ok().bodyValue(mapOf("token" to jwt)) }
            .onErrorResume { error -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue(mapOf("error" to error.localizedMessage)) }
    }

}
