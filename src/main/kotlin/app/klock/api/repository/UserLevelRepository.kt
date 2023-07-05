package app.klock.api.repository

import app.klock.api.domain.entity.UserLevel

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserLevelRepository : ReactiveCrudRepository<UserLevel, Long> {
    fun findByUserId(userId: Long): Mono<UserLevel>
}
