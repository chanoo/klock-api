package app.klock.api.repository

import app.klock.api.domain.entity.UserTimer
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface UserTimerRepository : ReactiveCrudRepository<UserTimer, Long> {
  fun findByUserId(userId: Long): Flux<UserTimer>
}
