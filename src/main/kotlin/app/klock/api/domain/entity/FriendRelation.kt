package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

/**
 * FriendRelation 엔티티는 친구 관계 정보를 저장하는 클래스입니다.
 * @property id 데이터베이스에서 자동 생성되는 기본 키 값
 * @property requesterId 친구 요청을 보낸 사용자의 고유 식별자
 * @property friendId 친구 요청을 받은 사용자의 고유 식별자
 * @property accepted 친구 요청 수락 여부 (기본값: true)
 * @property createdAt 레코드 생성 시간
 */
@Table("klk_friend_relation")
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
