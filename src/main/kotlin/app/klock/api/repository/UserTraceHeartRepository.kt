package app.klock.api.repository

import app.klock.api.domain.entity.UserTraceHeart
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserTraceHeartRepository : ReactiveCrudRepository<UserTraceHeart, Long> {
  fun findByUserTraceIdAndUserId(userTraceId: Long, userId: Long): Mono<UserTraceHeart>
}