package app.klock.api.repository

import app.klock.api.domain.entity.TimerPomodoro
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface TimerPomodoroRepository : ReactiveCrudRepository<TimerPomodoro, Long> {
  fun findAllByUserIdOrderBySeq(userId: Long): Flux<TimerPomodoro>
  fun deleteByUserId(userId: Long): Mono<Void>
}
