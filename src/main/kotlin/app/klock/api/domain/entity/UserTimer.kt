package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("klk_user_timer")
data class UserTimer(
  @Id
  val id: Long? = null,

  val type: UserTimerType,

  @Column("created_at")
  val createdAt: LocalDateTime,
)
