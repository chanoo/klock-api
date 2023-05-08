package app.klock.api.domain.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

/**
 * SocialLogin 엔티티는 소셜 로그인 정보를 저장하는 클래스입니다.
 * @property id 데이터베이스에서 자동 생성되는 기본 키 값
 * @property provider 소셜 로그인 제공자 (예: Google, Facebook 등)
 * @property providerUserId 소셜 로그인 제공자의 사용자 식별자
 * @property userId 해당 사용자의 고유 식별자
 * @property createdAt 레코드 생성 시간
 * @property updatedAt 레코드 수정 시간
 */
@Table("klk_social_login")
data class SocialLogin(
  @Id
  val id: Long? = null,

  @Column("provider")
  val provider: SocialProvider,

  @Column("provider_user_id")
  val providerUserId: String,

  @Column("user_id")
  val userId: Long,

  @CreatedDate
  @Column("created_at")
  val createdAt: LocalDateTime = LocalDateTime.now(),

  @LastModifiedDate
  @Column("updated_at")
  val updatedAt: LocalDateTime = LocalDateTime.now()
)
