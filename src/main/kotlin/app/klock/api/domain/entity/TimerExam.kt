package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("klk_timer_exam")
data class TimerExam(
  @Id
  val id: Long? = null,

  @Column("user_id")
  val userId: Long,

  @Column("name")
  val name: String,

  @Column("seq")
  val seq: Int,

  @Column("start_time")
  val startTime: LocalDateTime,

  @Column("duration")
  val duration: Int,

  @Column("question_count")
  val questionCount: Int,

  @Column("created_at")
  val createdAt: LocalDateTime? = null,

  @Column("updated_at")
  val updatedAt: LocalDateTime? = null
) {
  fun validate() {
    require(name.isNotBlank()) { "name must not be blank" }
    require(duration > 0) { "duration must be greater than 0" }
    require(questionCount > 0) { "questionCount must be greater than 0" }
    require(seq > 0) { "seq must be greater than 0" }
    require(userId > 0) { "userId must be greater than 0" }
  }
}
