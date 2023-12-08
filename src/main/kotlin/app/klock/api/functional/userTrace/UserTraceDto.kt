package app.klock.api.functional.userTrace

import java.time.LocalDateTime

data class UserTraceDto(
    val id: Long? = null,
    val userId: Long,
    val friendId: Long? = null,
    val friendNickName: String? = null,
    val friendImage: String? = null,
    val contents: String? = null,
    val contentsImage: String? = null,
    val heart: Boolean,
    val createdAt: LocalDateTime,
)

data class CreateUserTraceContent(
    val friendId: Long,
    val contents: String,
)

data class CreateUserTraceHeart(
    val friendId: Long,
)