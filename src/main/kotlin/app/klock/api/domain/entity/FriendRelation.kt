package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

/**
 * FriendRelation 엔티티는 팔로우 관계 정보를 저장하는 클래스입니다.
 * @property id 데이터베이스에서 자동 생성되는 기본 키 값
 * @property userId 사용자의 고유 식별자
 * @property followId 사용자의 팔로우 사용자의 고유 식별자
 * @property followed 맞팔로우 여부 (기본값: false)
 * @property createdAt 레코드 생성 시간
 */
@Table("klk_friend_relation")
data class FriendRelation(
  @Id
  val id: Long? = null,

  @Column("user_id")
  val userId: Long,

  @Column("follow_id")
  val followId: Long,

  val followed: Boolean = false,

  @Column("created_at")
  val createdAt: LocalDateTime = LocalDateTime.now()
)
