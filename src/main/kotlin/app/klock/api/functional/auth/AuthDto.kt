package app.klock.api.functional.auth.dto

open class BaseUserDto(open val name: String, open val email: String)

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String)

data class SocialLoginRequest(val accessToken: String)

data class UserRequest(val password: String, override val name: String, override val email: String) : BaseUserDto(name, email)
data class UserResponse(val id: Long?, override val name: String, override val email: String) : BaseUserDto(name, email)

data class CreateUserRequest(val password: String? = null, override val name: String, override val email: String) : BaseUserDto(name, email)
data class CreateUserResponse(val id: Long?, override val name: String, override val email: String) : BaseUserDto(name, email)

data class UpdateUserRequest(val id: Long?, val password: String, override val name: String, override val email: String) : BaseUserDto(name, email)
data class UpdateUserResponse(val id: Long?, override val name: String, override val email: String) : BaseUserDto(name, email)
