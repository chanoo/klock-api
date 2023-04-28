package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("klk_user_tag")
data class UserTag(
  @Id
  val id: Long? = null,

  @Column("user_id")
  val userId: Long,

  @Column("tag_id")
  val tagId: Long
)
