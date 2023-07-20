package app.klock.api.repository

import app.klock.api.domain.entity.TimerAuto
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface TimerAutoRepository : ReactiveCrudRepository<TimerAuto, Long> {
  fun findAllByUserIdOrderBySeq(userId: Long): Flux<TimerAuto>
  fun deleteByUserId(userId: Long): Mono<Void>
}
