package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("klk_user")
data class User(
  @Id
  val id: Long? = null,

  val email: String?,

  @Column("hashed_password")
  var hashedPassword: String? = null,

  val username: String,

  @Column("total_study_time")
  val totalStudyTime: Int,

  @Column("user_level_id")
  val userLevelId: Long,

  val role: UserRole,

  val active: Boolean,

  @Column("created_at")
  val createdAt: LocalDateTime,

  @Column("updated_at")
  val updatedAt: LocalDateTime
)
