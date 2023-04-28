package app.klock.api.handler

import app.klock.api.domain.entity.ChatBot
import app.klock.api.functional.chatBot.ChatBotDTO
import app.klock.api.service.ChatBotService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class ChatBotHandler(private val chatBotService: ChatBotService) {

  fun getByActiveChatBots(request: ServerRequest): Mono<ServerResponse> {
    val active = request.queryParam("active")
      .map { it.toBoolean() }
      .orElse(true)

    return chatBotService.getByActiveChatBots(active)
      .map(::chatBotToDTO) // 변환 함수를 사용하여 ChatBot 엔티티를 ChatBotDTO로 변환합니다.
      .collectList()
      .flatMap { chatBots -> ServerResponse.ok().body(BodyInserters.fromValue(chatBots)) }
  }

  private fun chatBotToDTO(chatBot: ChatBot): ChatBotDTO {
    return ChatBotDTO(
      id = chatBot.id,
      subject = chatBot.subject,
      name = chatBot.name,
      chatBotImageUrl = chatBot.chatBotImageUrl,
      title = chatBot.title,
      persona = chatBot.persona
    )
  }

}
