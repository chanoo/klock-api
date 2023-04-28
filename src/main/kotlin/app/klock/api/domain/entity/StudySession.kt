package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("klk_study_session")
data class StudySession(
  @Id
  val id: Long? = null,

  @Column("user_id")
  val userId: Long,

  @Column("start_time")
  val startTime: LocalDateTime,

  @Column("end_time")
  val endTime: LocalDateTime
)
