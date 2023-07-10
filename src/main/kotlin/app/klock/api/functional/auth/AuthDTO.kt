package app.klock.api.functional.auth

import app.klock.api.domain.entity.*
import java.time.DayOfWeek

data class AuthDTO(
  val id: Long? = null,
  val nickname: String,
  val email: String?
)

open class BaseUserDTO(open val nickname: String, open val email: String?)

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String)

data class SocialLoginRequest(val accessToken: String)
data class SocialLoginResponse(val accessToken: String, val refreshToken: String)

data class SignUp(
  val savedUser: User,
  val savedSocialLogin: SocialLogin,
  val savedUserSetting: UserSetting,
  val savedUserTag: UserTag?
)

data class SignUpReqDTO(
  val nickname: String,
  val providerUserId: String,
  val provider: SocialProvider,
  val email: String?,
  val password: String?,
  val tagId: Long,
  val startOfTheWeek: DayOfWeek,
  val startOfTheDay: Int
)

data class SignUpResDTO(
  val id: Long,
  val accessToken: String,
  val refreshToken: String,
  val nickname: String,
  val providerUserId: String,
  val provider: SocialProvider,
  val email: String?,
  val tagId: Long?,
  val startOfTheWeek: DayOfWeek,
  val startOfTheDay: Int
)

data class LoginDto(
  val token: String,
  val userId: Long?
)

data class RefreshTokenRequest(val refreshToken: String)
data class RefreshTokenResponse(val accessToken: String, val refreshToken: String)
