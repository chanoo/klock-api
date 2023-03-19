package app.klock.api.functional.auth.dto

data class AuthDto (
    val id: Long? = null,
    val username: String,
    val email: String
)
open class BaseUserDto(open val name: String, open val email: String)

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String)

data class SocialLoginRequest(val accessToken: String)

data class UserResponse(val id: Long?, override val name: String, override val email: String) : BaseUserDto(name, email)

data class CreateUserRequest(val username: String, val email: String, val password: String? = null)

data class UpdateUserRequest(val id: Long?, val password: String, override val name: String, override val email: String) : BaseUserDto(name, email)
data class UpdateUserResponse(val id: Long?, override val name: String, override val email: String) : BaseUserDto(name, email)

data class RefreshTokenRequest(val refreshToken: String)
