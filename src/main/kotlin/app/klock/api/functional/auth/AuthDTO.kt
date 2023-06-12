package app.klock.api.functional.auth

import app.klock.api.domain.entity.*
import java.time.DayOfWeek

data class AuthDTO(
  val id: Long? = null,
  val nickName: String,
  val email: String?
)

open class BaseUserDTO(open val nickName: String, open val email: String?)

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
  val nickName: String,
  val providerUserId: String,
  val provider: SocialProvider,
  val email: String?,
  val password: String?,
  val tagId: Long?,
  val startOfTheWeek: DayOfWeek,
  val startOfTheDay: Int
)
data class SignUpResDTO(
  val id: Long,
  val accessToken: String,
  val refreshToken: String,
  val nickName: String,
  val providerUserId: String,
  val provider: SocialProvider,
  val email: String?,
  val tagId: Long?
)

data class UpdateUserRequest(val id: Long?, val password: String, override val nickName: String, override val email: String) : BaseUserDTO(nickName, email)
data class UpdateUserResponse(val id: Long?, override val nickName: String, override val email: String?) : BaseUserDTO(nickName, email)

data class RefreshTokenRequest(val refreshToken: String)
data class RefreshTokenResponse(val accessToken: String, val refreshToken: String)
