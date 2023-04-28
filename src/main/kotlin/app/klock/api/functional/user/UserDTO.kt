package app.klock.api.functional.auth.dto

import app.klock.api.domain.entity.User

data class ChangePasswordRequest(
  val currentPassword: String,
  val newPassword: String
)

data class UserResponse(
  val id: Long? = null,
  val username: String,
  val email: String? = null
) {
  companion object {
    fun from(user: User): UserResponse {
      return UserResponse(
        id = user.id,
        username = user.username,
        email = user.email
      )
    }
  }
}
