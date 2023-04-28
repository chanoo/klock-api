package app.klock.api.domain.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
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

  @CreatedDate
  @Column("created_at")
  val createdAt: LocalDateTime = LocalDateTime.now(),

  @LastModifiedDate
  @Column("updated_at")
  val updatedAt: LocalDateTime = LocalDateTime.now()
)
