package app.klock.api.functional.chatBot

import app.klock.api.handler.ChatBotHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class ChatBotRouter(private val chatBotHandler: ChatBotHandler) {

  @Bean
  fun chatBotRoutes() = router {
    "/api/v1/chat-bots".nest {
      GET("", chatBotHandler::getByActiveChatBots)
    }
  }
}
