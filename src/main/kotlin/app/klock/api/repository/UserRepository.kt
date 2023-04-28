package app.klock.api.repository

import app.klock.api.domain.entity.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserRepository : ReactiveCrudRepository<User, Long> {
  fun findByEmail(email: String): Mono<User>
}
