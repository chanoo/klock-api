package app.klock.api.repository

import app.klock.api.domain.entity.UserTrace
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserTraceRepository : ReactiveCrudRepository<UserTrace, Long> {
  fun findByUserId(userId: Long): Mono<UserTrace>
}