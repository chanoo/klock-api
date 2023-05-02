package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("klk_timer_focus")
data class TimerFocus(
  @Id
  val id: Long? = null,

  @Column("user_id")
  val userId: Long,

  @Column("seq")
  val seq: Int,

  @Column("name")
  val name: String,

  @Column("created_at")
  val createdAt: LocalDateTime? = null,

  @Column("updated_at")
  val updatedAt: LocalDateTime? = null
) {
  fun validate() {
    require(name.isNotBlank()) { "name must not be blank" }
    require(seq > 0) { "seq must be greater than 0" }
    require(userId > 0) { "userId must be greater than 0" }
  }
}
