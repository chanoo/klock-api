package app.klock.api.functional.chatbot

data class ChatBotDTO(
  val id: Long? = null,
  val subject: String,
  val name: String,
  val title: String,
  val persona: String
)
