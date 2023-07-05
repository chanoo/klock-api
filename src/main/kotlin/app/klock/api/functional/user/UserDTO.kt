package app.klock.api.functional.user

import app.klock.api.domain.entity.User
import app.klock.api.domain.entity.UserLevel
import app.klock.api.domain.entity.UserSetting
import app.klock.api.domain.entity.UserTag
import app.klock.api.functional.auth.BaseUserDTO
import java.time.DayOfWeek

data class ChangePasswordRequest(
  val currentPassword: String,
  val newPassword: String
)

data class UserInfoDto(
  val id: Long? = null,
  val nickName: String,
  val email: String? = null,
  val level: Int,
  val requiredStudyTime: Int,
  val characterName: String,
  val characterImage: String,
  val startOfTheWeek: DayOfWeek,
  val startOfTheDay: Int,
  val tagId: Long
) {
  companion object {
    fun from(user: User,
             userLevel: UserLevel? = null,
             userSetting: UserSetting? = null,
             userTag: UserTag? = null): UserInfoDto {
      return UserInfoDto(
        id = user.id,
        nickName = user.nickName,
        email = user.email,
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

data class CheckNickNameRequest(
  val nickName: String
)

data class UpdateUserRequest(
  val nickName: String,
  val tagId: Long,
  val startOfTheWeek: DayOfWeek,
  val startOfTheDay: Int
)
data class UpdateUserResponse(
  val id: Long?,
  override val nickName: String,
  override val email: String?,
  val tagId: Long?,
  val startOfTheWeek: DayOfWeek,
  val startOfTheDay: Int
) : BaseUserDTO(nickName, email)
