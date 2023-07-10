package app.klock.api.repository

import app.klock.api.domain.entity.TimerFocus
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface TimerFocusRepository : ReactiveCrudRepository<TimerFocus, Long> {
  fun findAllByUserIdOrderBySeq(userId: Long): Flux<TimerFocus>
  fun deleteByUserId(userId: Long): Mono<Void>
}
