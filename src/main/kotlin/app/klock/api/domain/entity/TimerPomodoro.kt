package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

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

  @Column("rest_time")
  val restTime: Int,

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
    require(restTime > 0) { "restTime must be greater than 0" }
    require(cycleCount > 0) { "cycleCount must be greater than 0" }
  }
}
