package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

/**
 * UserTag 엔티티는 사용자와 태그 간의 관계를 저장하는 클래스입니다.
 * @property id 데이터베이스에서 자동 생성되는 기본 키 값
 * @property userId 해당 사용자의 고유 식별자
 * @property tagId 해당 태그의 고유 식별자
 */
@Table("klk_user_tag")
data class UserTag(
  @Id
  val id: Long? = null,

  @Column("user_id")
  val userId: Long,

  @Column("tag_id")
  val tagId: Long
)
