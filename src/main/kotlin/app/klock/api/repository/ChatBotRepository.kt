package app.klock.api.repository

import app.klock.api.domain.entity.ChatBot
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface ChatBotRepository : ReactiveCrudRepository<ChatBot, Long> {
  fun findByActive(active: Boolean): Flux<ChatBot>
}