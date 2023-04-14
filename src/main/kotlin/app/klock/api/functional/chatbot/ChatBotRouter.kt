package app.klock.api.functional.chatbot

import app.klock.api.handler.ChatBotHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class ChatBotRouter(private val chatBotHandler: ChatBotHandler) {

  @Bean
  fun chatBotRoutes() = router {
    "/api/chatbots".nest {
      GET("", chatBotHandler::getByActiveChatBots)
    }
  }
}
