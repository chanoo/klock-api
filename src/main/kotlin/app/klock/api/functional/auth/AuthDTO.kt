package app.klock.api.functional.auth.dto

import app.klock.api.domain.entity.SocialProvider

data class AuthDTO (
    val id: Long? = null,
    val username: String,
    val email: String?
)
open class BaseUserDTO(open val name: String, open val email: String?)

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String)

data class SocialLoginRequest(val accessToken: String)
data class SocialLoginResponse(val accessToken: String, val refreshToken: String)

data class UserResponse(val id: Long?, override val name: String, override val email: String?) : BaseUserDTO(name, email)

data class SignUpReqDTO(val username: String, val providerUserId: String, val provider: SocialProvider, val email: String?, val password: String?, val tagId: Long?)
data class SignUpResDTO(val id: Long, val username: String, val providerUserId: String, val provider: SocialProvider, val email: String?, val tagId: Long?)

data class UpdateUserRequest(val id: Long?, val password: String, override val name: String, override val email: String) : BaseUserDTO(name, email)
data class UpdateUserResponse(val id: Long?, override val name: String, override val email: String?) : BaseUserDTO(name, email)

data class RefreshTokenRequest(val refreshToken: String)
data class RefreshTokenResponse(val accessToken: String, val refreshToken: String)
