package app.klock.api.functional.userTrace

import app.klock.api.domain.entity.User
import app.klock.api.domain.entity.UserTrace
import app.klock.api.domain.entity.UserTraceType
import java.time.LocalDateTime

data class UserTraceDto(
  val id: Long? = null,
  val writeUserId: Long? = null,
  val writeNickname: String? = null,
  val writeUserImage: String? = null,
  val type: UserTraceType,
  val contents: String? = null,
  val contentsImage: String? = null,
  val heart: Boolean,
  val createdAt: LocalDateTime,
) {
  companion object {
    fun from(
      userTrace: UserTrace,
      user: User
    ): UserTraceDto {
      return UserTraceDto(
        id = userTrace.id,
        writeUserId = userTrace.writeUserId,
        writeNickname = user.nickname,
        writeUserImage = user.profileImage,
        type = userTrace.type,
        contents = userTrace.contents,
        contentsImage = userTrace.contentsImage,
        heart = userTrace.heart,
        createdAt = userTrace.createdAt
      )
    }
  }
}

data class CreateContentTrace(
  val writeUserId: Long,
  val type: UserTraceType,
  val contents: String? = null,
)

data class UpdateHeartTrace(
  val writeUserId: Long,
)

data class CreateStudyTrace(
  val contents: String,
)