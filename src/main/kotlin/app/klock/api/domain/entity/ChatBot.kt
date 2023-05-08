package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

/**
 * ChatBot 엔티티는 채팅 봇 정보를 저장하는 클래스입니다.
 * @property id 데이터베이스에서 자동 생성되는 기본 키 값
 * @property subject 채팅 봇의 주제
 * @property name 채팅 봇의 이름
 * @property title 채팅 봇의 제목
 * @property chatBotImageUrl 채팅 봇 이미지의 URL
 * @property persona 채팅 봇의 인격(페르소나)
 * @property active 채팅 봇의 활성화 여부
 * @property createdAt 레코드 생성 시간
 * @property updatedAt 레코드 수정 시간
 */
@Table("klk_chat_bot")
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
