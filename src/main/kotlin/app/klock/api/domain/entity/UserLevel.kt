package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

/**
 * UserLevel 엔티티는 사용자의 레벨 정보를 저장하는 클래스입니다.
 * @property id 데이터베이스에서 자동 생성되는 기본 키 값
 * @property userId 사용자 ID
 * @property level 사용자의 레벨
 * @property requiredStudyTime 레벨 업에 필요한 학습 시간 (분 단위)
 * @property characterName 레벨에 해당하는 캐릭터의 이름
 * @property characterImage 레벨에 해당하는 캐릭터의 이미지
 */
@Table("klk_user_level")
data class UserLevel(
  @Id
  val id: Long? = null,

  @Column("user_id")
  val userId: Long,

  @Column("level")
  val level: Int,

  @Column("required_study_time")
  val requiredStudyTime: Int,

  @Column("character_name")
  val characterName: String,

  @Column("character_image")
  val characterImage: String
)
