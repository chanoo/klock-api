package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

/**
 * UserTraceHeart 엔티티는 사용자의 흔적의 좋아요를 기록하는 클래스입니다.
 * @property id 데이터베이스에서 자동 생성되는 기본 키 값
 * @property userTraceId 사용자 흔적 고유 식별번호
 * @property userId 사용자 고유 식별번호
 * @property heartCount 좋아요 갯수
 */

@Table("klk_user_trace_heart")
data class UserTraceHeart(
  @Id
  val id: Long? = null,

  @Column("user_trace_id")
  val userTraceId: Long,

  @Column("user_id")
  val userId: Long,

  @Column("heart_count")
  val heartCount: Int,
)