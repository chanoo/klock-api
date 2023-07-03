package app.klock.api.functional.user

import app.klock.api.domain.entity.User

data class ChangePasswordRequest(
  val currentPassword: String,
  val newPassword: String
)

data class UserResponse(
  val id: Long? = null,
  val nickName: String,
  val email: String? = null
) {
  companion object {
    fun from(user: User): UserResponse {
      return UserResponse(
        id = user.id,
        nickName = user.nickName,
        email = user.email
      )
    }
  }
}

data class CheckNickNameRequest(
  val nickName: String
)
