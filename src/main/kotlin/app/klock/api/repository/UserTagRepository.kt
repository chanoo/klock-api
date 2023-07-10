package app.klock.api.repository

import app.klock.api.domain.entity.UserTag
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserTagRepository : ReactiveCrudRepository<UserTag, Long> {
  fun findByUserId(userId: Long): Mono<UserTag>
  fun deleteByUserId(userId: Long): Mono<Void>
}