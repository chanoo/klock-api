package app.klock.api.functional.friendRelation

import app.klock.api.domain.entity.FriendRelation
import app.klock.api.domain.entity.User
import java.time.LocalDateTime

data class FriendRelationRequest(
    val followId: Long
)

data class FriendRelationDto(
    val id: Long? = null,
    val userId: Long,
    val followId: Long,
    val followed: Boolean,
    val profileImage: String? = null,
    val nickname: String? = null,
    val createdAt: LocalDateTime
) {
    fun toDomain() = FriendRelationDto(id, userId, followId, followed, profileImage, nickname, createdAt)

    companion object {
        fun from(domain: FriendRelation, follow: User) = FriendRelationDto(
            domain.id,
            domain.userId,
            domain.followId,
            domain.followed,
            follow.profileImage,
            follow.nickname,
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

data class FollowFromQrCodeRequest(
    val followData: String,
    val encryptedKey: String
)

data class FollowQrCodeData(
    val issueDate: String,
    val expireDate: String,
    val followId: Long
)