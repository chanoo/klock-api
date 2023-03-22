package app.klock.api.functional.friemdRelation

data class FriendRelationRequest(
    val requesterId: Long,
    val friendId: Long
)
