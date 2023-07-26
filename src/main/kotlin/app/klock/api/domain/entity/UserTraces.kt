package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

/**
 * UserTraces 엔티티는 사용자의 흔적(공부, 친구)들을 기록하는 클래스입니다.
 * @property id 데이터베이스에서 자동 생성되는 기본 키 값
 * @property userId 사용자 고유 식별번호
 * @property friendId 친구 고유 식별번호
 * @property friendNickName 친구 닉네임
 * @property contents 흔적 내용 텍스트
 * @property contentsImage 흔적 내용 이미지
 * @property like 좋아요 여부
 * @property createdAt 레코드 생성 시간
 */

@Table("klk_user_traces")
data class UserTraces(
  @Id
  val id: Long? = null,

  @Column("user_id")
  val userId: Long,

  @Column("friend_id")
  val friendId: Long,

  @Column("friend_nickname")
  val friendNickName: String,

  @Column("contents")
  val contents: String,

  @Column("contents_image")
  val contentsImage: String,

  @Column("like")
  val like: Boolean,

  @Column("created_at")
  val createdAt: LocalDateTime,
)