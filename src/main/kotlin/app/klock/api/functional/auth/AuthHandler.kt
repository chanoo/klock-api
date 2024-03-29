package app.klock.api.functional.auth

import app.klock.api.domain.entity.UserSetting
import app.klock.api.domain.entity.UserTag
import app.klock.api.service.AuthService
import app.klock.api.service.UserService
import app.klock.api.service.UserSettingService
import app.klock.api.service.UserTagService
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
  private val userService: UserService,
  private val userTagService: UserTagService,
  private val userSettingService: UserSettingService,
  private val jwtUtils: JwtUtils
) {

  // 회원가입 요청 처리
  fun signup(request: ServerRequest): Mono<ServerResponse> =
    request.bodyToMono(SignUpReqDTO::class.java)
      .flatMap { signUpRequest ->
        val savedUserMono = authService.signup(
          nickname = signUpRequest.nickname,
          email = signUpRequest.email,
          password = signUpRequest.password
        )
        savedUserMono.flatMap { savedUser ->
          Mono.zip(
            authService.createSocialLogin(
              userId = savedUser.id!!,
              provider = signUpRequest.provider,
              providerUserId = signUpRequest.providerUserId
            ),
            userSettingService.create(
              UserSetting(
                userId = savedUser.id!!,
                startOfTheWeek = signUpRequest.startOfTheWeek,
                startOfTheDay = signUpRequest.startOfTheDay
              )
            ),
            userTagService.create(
              UserTag(
                userId = savedUser.id,
                tagId = signUpRequest.tagId
              )
            )
          ).map { results ->
            SignUp(savedUser, results.t1, results.t2, results.t3)
          }
        }
      }
      .flatMap { (user, socialLogin, userSetting, userTag) ->
        val accessToken = jwtUtils.generateToken(user.id.toString(), listOf(user.role.name))
        val refreshToken = jwtUtils.generateRefreshToken(user.id.toString(), listOf(user.role.name))
        ServerResponse.status(HttpStatus.CREATED).bodyValue(
          SignUpResDTO(
            id = user.id!!,
            accessToken = accessToken,
            refreshToken = refreshToken,
            nickname = user.nickname,
            provider = socialLogin.provider,
            providerUserId = socialLogin.providerUserId,
            email = user.email,
            tagId = userTag?.tagId,
            startOfTheWeek = userSetting.startOfTheWeek,
            startOfTheDay = userSetting.startOfTheDay
          )
        )
      }
      .onErrorResume { error ->
        ServerResponse.badRequest()
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(mapOf("error" to error.message))
      }

  // 로그인 요청 처리
  fun signin(request: ServerRequest): Mono<ServerResponse> {
    return request.bodyToMono(LoginRequest::class.java)
      .flatMap { loginRequest ->
        userService.findByEmail(loginRequest.email)
          .filter { user ->
            userService.validatePassword(
              loginRequest
                .password, user.hashedPassword
            )
          }
          .switchIfEmpty(Mono.error(Exception("Invalid nickname or password")))
          .flatMap { user ->
            val token = jwtUtils.generateToken(user.id.toString(), listOf(user.role.name))
            ServerResponse.ok()
              .contentType(MediaType.APPLICATION_JSON)
              .bodyValue(LoginDto(token = token, userId = user.id, publicKey = null))
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
          .flatMap { accessToken ->
            val userId = jwtUtils.getUserIdFromToken(refreshTokenRequest.refreshToken)
            val roles = jwtUtils.getAuthoritiesFromJwt(refreshTokenRequest.refreshToken).map { it.authority }
            val newRefreshToken = jwtUtils.generateRefreshToken(userId, roles)
            val refreshTokenResponse = RefreshTokenResponse(accessToken, newRefreshToken)
            ServerResponse.ok().bodyValue(refreshTokenResponse)
          }
      }
      .onErrorResume { error ->
        ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue(mapOf("error" to error.localizedMessage))
      }
  }

  // 카카오 로그인 요청 처리
  fun authenticateSocial(request: ServerRequest): Mono<ServerResponse> {
    val socialLoginRequest = request.bodyToMono(SocialLoginRequest::class.java)
    return authService.authenticateSocial(socialLoginRequest)
      .flatMap { login -> ServerResponse.ok().bodyValue(login) }
      .onErrorResume { error ->
        ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue(mapOf("error" to error.localizedMessage))
      }
  }

  // 애플 로그인 요청 처리
  fun authenticateApple(request: ServerRequest): Mono<ServerResponse> {
    val appleLoginRequest = request.bodyToMono(AppleLoginRequest::class.java)
    return authService.authenticateApple(appleLoginRequest)
      .flatMap { login -> ServerResponse.ok().bodyValue(login) }
      .onErrorResume { error ->
        ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue(mapOf("error" to error.localizedMessage))
      }
  }

}
