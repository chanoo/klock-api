package app.klock.api.repository

import app.klock.api.domain.entity.TimerFocus
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface TimerFocusRepository : ReactiveCrudRepository<TimerFocus, Long> {
  fun findAllByUserIdOrderBySeq(userId: Long): Flux<TimerFocus>
}
