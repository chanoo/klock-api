package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

/**
 * User 엔티티는 사용자 정보를 저장하는 클래스입니다.
 * @property id 데이터베이스에서 자동 생성되는 기본 키 값
 * @property email 사용자의 이메일 주소
 * @property hashedPassword 사용자의 해시된 비밀번호
 * @property nickname 사용자의 닉네임
 * @property totalStudyTime 사용자의 총 학습 시간 (분 단위)
 * @property userLevelId 사용자의 레벨 정보와 연결된 고유 식별자
 * @property profileImage 사용자의 프로필 이미지
 * @property role 사용자의 권한
 * @property active 사용자 계정 활성화 여부
 * @property createdAt 레코드 생성 시간
 * @property updatedAt 레코드 수정 시간
 */
@Table("klk_user")
data class User(
  @Id
  val id: Long? = null,

  @Column("email")
  val email: String?,

  @Column("hashed_password")
  var hashedPassword: String? = null,

  @Column("nickname")
  val nickname: String,

  @Column("total_study_time")
  val totalStudyTime: Int,

  @Column("user_level_id")
  val userLevelId: Long,

  @Column("profile_image")
  val profileImage: String? = null,

  @Column("role")
  val role: UserRole,

  @Column("active")
  val active: Boolean,

  @Column("created_at")
  val createdAt: LocalDateTime,

  @Column("updated_at")
  val updatedAt: LocalDateTime
) {
  companion object {
    fun allowedPattern() : Regex {
      return Regex("[a-zA-Z0-9가-힣]+")
    }
    fun allowedNicknameMaxLength() : Int {
      return 10
    }
  }

}
