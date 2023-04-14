package app.klock.api.service

import app.klock.api.domain.entity.ChatBot
import app.klock.api.repository.ChatBotRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class ChatBotService(private val chatBotRepository: ChatBotRepository) {

  fun getByActiveChatBots(active: Boolean): Flux<ChatBot> {
    return chatBotRepository.findByActive(active)
  }

}
