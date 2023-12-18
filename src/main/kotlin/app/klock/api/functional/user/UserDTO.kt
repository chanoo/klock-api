package app.klock.api.functional.user

import app.klock.api.domain.entity.User
import app.klock.api.domain.entity.UserLevel
import app.klock.api.domain.entity.UserSetting
import app.klock.api.domain.entity.UserTag
import app.klock.api.functional.auth.BaseUserDTO
import java.time.DayOfWeek
import java.time.LocalDateTime

data class ChangePasswordRequest(
  val currentPassword: String,
  val newPassword: String
)

data class UserInfoDto(
  val id: Long? = null,
  val nickname: String,
  val email: String? = null,
  val profileImage: String? = null,
  val level: Int,
  val requiredStudyTime: Int,
  val characterName: String,
  val characterImage: String,
  val startOfTheWeek: DayOfWeek,
  val startOfTheDay: Int,
  val tagId: Long
) {
  companion object {
    fun from(
      user: User,
      userLevel: UserLevel? = null,
      userSetting: UserSetting? = null,
      userTag: UserTag? = null
    ): UserInfoDto {
      return UserInfoDto(
        id = user.id,
        nickname = user.nickname,
        email = user.email,
        profileImage = user.profileImage,
        level = userLevel?.level ?: 0,
        requiredStudyTime = userLevel?.requiredStudyTime ?: 0,
        characterName = userLevel?.characterName ?: "",
        characterImage = userLevel?.characterImage ?: "",
        startOfTheWeek = userSetting?.startOfTheWeek ?: DayOfWeek.SUNDAY,
        startOfTheDay = userSetting?.startOfTheDay ?: 0,
        tagId = userTag?.tagId ?: 0
      )
    }
  }
}

data class CheckNicknameRequest(
  val nickname: String
)

data class UpdateUserRequest(
  val nickname: String,
  val tagId: Long,
  val startOfTheWeek: DayOfWeek,
  val startOfTheDay: Int,
  val updatedAt: LocalDateTime = LocalDateTime.now()
)

data class UpdateUserResponse(
  val id: Long?,
  override val nickname: String,
  override val email: String?,
  val tagId: Long?,
  val startOfTheWeek: DayOfWeek,
  val startOfTheDay: Int
) : BaseUserDTO(nickname, email)
