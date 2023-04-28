package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("klk_timer_exam")
data class TimerExam(
  @Id
  val id: Long? = null,

  @Column("user_timer_id")
  val userTimerId: Long,

  val name: String,

  @Column("start_time")
  val startTime: LocalDateTime,

  @Column("duration")
  val duration: Int,

  @Column("question_count")
  val questionCount: Int,
)
