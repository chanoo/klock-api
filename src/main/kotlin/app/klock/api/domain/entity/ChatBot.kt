package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("chat_bot")
data class ChatBot(
  @Id
  val id: Long? = null,
  val subject: String,
  val name: String,
  val title: String,
  @Column("chat_bot_image_url")
  val chatBotImageUrl: String,
  val persona: String,
  val active: Boolean,
  @Column("created_at")
  val createdAt: LocalDateTime,
  @Column("updated_at")
  val updatedAt: LocalDateTime
)
