package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("klk_timer_study")
data class TimerStudy(
  @Id
  val id: Long? = null,

  @Column("user_timer_id")
  val userTimerId: Long,

  val name: String,
)
