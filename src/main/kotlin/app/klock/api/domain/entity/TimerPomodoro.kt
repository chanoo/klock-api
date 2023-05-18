package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

/**
 * TimerPomodoro 엔티티는 사용자의 타이머 포모도로 정보를 저장하는 클래스입니다.
 * @property id 데이터베이스에서 자동 생성되는 기본 키 값
 * @property userId 해당 사용자의 고유 식별자
 * @property name 타이머 포모도로의 이름
 * @property seq 타이머 포모도로의 순서
 * @property focusTime 집중 시간 (분 단위)
 * @property breakTime 휴식 시간 (분 단위)
 * @property cycleCount 포모도로 사이클 횟수
 * @property createdAt 레코드 생성 시간
 * @property updatedAt 레코드 수정 시간
 */
@Table("klk_timer_pomodoro")
data class TimerPomodoro(
  @Id
  val id: Long? = null,

  @Column("user_id")
  val userId: Long,

  @Column("name")
  val name: String,

  @Column("seq")
  val seq: Int,

  @Column("focus_time")
  val focusTime: Int,

  @Column("break_time")
  val breakTime: Int,

  @Column("cycle_count")
  val cycleCount: Int,

  @Column("created_at")
  val createdAt: LocalDateTime? = null,

  @Column("updated_at")
  val updatedAt: LocalDateTime? = null

) {
  fun validate() {
    require(name.isNotBlank()) { "name must not be blank" }
    require(focusTime > 0) { "focusTime must be greater than 0" }
    require(breakTime > 0) { "breakTime must be greater than 0" }
    require(cycleCount > 0) { "cycleCount must be greater than 0" }
    require(seq > 0) { "seq must be greater than 0" }
  }
}
