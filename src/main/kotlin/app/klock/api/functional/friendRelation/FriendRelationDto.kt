package app.klock.api.functional.friendRelation

import app.klock.api.domain.entity.FriendRelation
import java.time.LocalDateTime

data class FriendRelationRequest(
    val followId: Long
)

data class FriendRelationDto(
    val id: Long? = null,
    val userId: Long,
    val followId: Long,
    val followed: Boolean,
    val createdAt: LocalDateTime
) {
    fun toDomain() = FriendRelationDto(id, userId, followId, followed, createdAt)

    companion object {
        fun from(domain: FriendRelation) = FriendRelationDto(
            domain.id,
            domain.userId,
            domain.followId,
            domain.followed,
            domain.createdAt
        )
    }
}

data class FriendDetailDto(
    val followId: Long,
    val nickname: String,
    val totalStudyTime: Int,
    val profileImage: String,
)