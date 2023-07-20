package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

/**
 * TimerAuto 엔티티는 사용자의 자동시간 타이머 정보를 저장하는 클래스입니다.
 * @property id 데이터베이스에서 자동 생성되는 기본 키 값
 * @property userId 해당 사용자의 고유 식별자
 * @property seq 타이머의 순서
 * @property name 타이머의 이름
 * @property createdAt 레코드 생성 시간
 * @property updatedAt 레코드 수정 시간
 */
@Table("klk_timer_auto")
data class TimerAuto(
  @Id
  val id: Long? = null,

  @Column("user_id")
  val userId: Long,

  @Column("seq")
  val seq: Int,

  @Column("name")
  val name: String,

  @Column("created_at")
  val createdAt: LocalDateTime? = null,

  @Column("updated_at")
  val updatedAt: LocalDateTime? = null
) {
  fun validate() {
    require(name.isNotBlank()) { "name must not be blank" }
    require(seq > 0) { "seq must be greater than 0" }
    require(userId > 0) { "userId must be greater than 0" }
  }
}
