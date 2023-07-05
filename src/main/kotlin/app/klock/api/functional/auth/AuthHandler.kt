package app.klock.api.functional.auth

import app.klock.api.domain.entity.UserLevel
import app.klock.api.domain.entity.UserSetting
import app.klock.api.domain.entity.UserTag
import app.klock.api.service.*
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
  private val userLevelService: UserLevelService,
  private val jwtUtils: JwtUtils
) {

  // 회원가입 요청 처리
  fun signup(request: ServerRequest): Mono<ServerResponse> =
    request.bodyToMono(SignUpReqDTO::class.java)
      .flatMap { signUpRequest ->
        authService.signup(
          nickName = signUpRequest.nickName,
          email = signUpRequest.email,
          password = signUpRequest.password
        ).flatMap { savedUser ->
          authService.createSocialLogin(
            userId = savedUser.id!!,
            provider = signUpRequest.provider,
            providerUserId = signUpRequest.providerUserId
          ).flatMap { savedSocialLogin ->
            userSettingService.create(
              UserSetting(
                userId = savedUser.id!!,
                startOfTheWeek = signUpRequest.startOfTheWeek,
                startOfTheDay = signUpRequest.startOfTheDay
              )
            ).flatMap { savedUserSetting ->
              userLevelService.create(
                UserLevel(
                  userId = savedUser.id!!,
                  level = 1,
                  requiredStudyTime = 0,
                  characterName = "",
                  characterImage = ""
                )
              ).flatMap { savedUserLevel ->
                if (signUpRequest.tagId != null) {
                  userTagService.create(
                    UserTag(
                      userId = savedUser.id,
                      tagId = signUpRequest.tagId
                    )
                  ).map { savedUserTag ->
                    SignUp(savedUser, savedSocialLogin, savedUserSetting, savedUserLevel, savedUserTag)
                  }
                } else {
                  Mono.just(SignUp(savedUser, savedSocialLogin, savedUserSetting, savedUserLevel, null))
                }
              }
            }
          }
        }
      }
      .flatMap { (user, socialLogin, userSetting, userLevel, userTag) ->
        val accessToken = jwtUtils.generateToken(user.id.toString(), listOf(user.role.name))
        val refreshToken = jwtUtils.generateRefreshToken(user.id.toString(), listOf(user.role.name))
        ServerResponse.status(HttpStatus.CREATED).bodyValue(
          SignUpResDTO(id = user.id!!,
            accessToken = accessToken,
            refreshToken = refreshToken,
            nickName = user.nickName,
            provider = socialLogin.provider,
            providerUserId = socialLogin.providerUserId,
            email = user.email,
            tagId = userTag?.tagId)
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
            userService.validatePassword(loginRequest
              .password, user.hashedPassword)
          }
          .switchIfEmpty(Mono.error(Exception("Invalid nickname or password")))
          .flatMap { user ->
            val token = jwtUtils.generateToken(user.id.toString(), listOf(user.role.name))
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
