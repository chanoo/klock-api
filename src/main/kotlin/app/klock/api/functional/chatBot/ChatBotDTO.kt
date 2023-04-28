package app.klock.api.functional.chatBot

data class ChatBotDTO(
  val id: Long? = null,
  val subject: String,
  val name: String,
  val chatBotImageUrl: String,
  val title: String,
  val persona: String
)
