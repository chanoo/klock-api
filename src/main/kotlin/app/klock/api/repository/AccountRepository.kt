package app.klock.api.repository

import app.klock.api.domain.entity.Account
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface AccountRepository : ReactiveCrudRepository<Account, Long> {
    fun findByEmail(email: String): Mono<Account>
}
