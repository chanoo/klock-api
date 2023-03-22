package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("friend_relation")
data class FriendRelation(
    @Id
    val id: Long? = null,
    @Column("requester_id")
    val requesterId: Long,
    @Column("friend_id")
    val friendId: Long,
    val accepted: Boolean = true,
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)
