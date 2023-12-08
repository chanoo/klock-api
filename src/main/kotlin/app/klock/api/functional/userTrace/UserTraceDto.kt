package app.klock.api.functional.userTrace

import app.klock.api.domain.entity.*
import app.klock.api.functional.user.UserInfoDto
import java.time.DayOfWeek
import java.time.LocalDateTime

data class UserTraceDto(
    val id: Long? = null,
    val writeUserId: Long? = null,
    val writeUserImage: String? = null,
    val contents: String? = null,
    val contentsImage: String? = null,
    val heart: Boolean,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(userTrace: UserTrace,
                 user: User): UserTraceDto {
            return UserTraceDto(
                id = userTrace.id,
                writeUserId = userTrace.writeUserId,
                writeUserImage = user.profileImage,
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
    val contents: String,
)

data class UpdateHeartTrace(
    val writeUserId: Long,
)

data class CreateStudyTrace(
    val contents: String,
)