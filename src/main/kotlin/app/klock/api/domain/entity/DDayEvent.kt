package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Table("klk_d_day_event")
data class DDayEvent(
  @Id
  val id: Long? = null,

  @Column("user_id")
  val userId: Long,

  @Column("event_name")
  val eventName: String,

  @Column("event_date")
  val eventDate: LocalDate,

  @Column("created_at")
  val createdAt: LocalDateTime
)
