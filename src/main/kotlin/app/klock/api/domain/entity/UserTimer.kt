package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("klk_user_timer")
data class UserTimer(
  @Id
  val id: Long? = null,

  @Column("user_id")
  val userId: Long,

  @Column("type")
  val type: UserTimerType,

  @Column("seq")
  val seq: Int,
)
