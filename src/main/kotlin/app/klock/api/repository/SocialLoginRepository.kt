package app.klock.api.repository

import app.klock.api.domain.entity.SocialLogin
import app.klock.api.domain.entity.SocialProvider
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface SocialLoginRepository : ReactiveCrudRepository<SocialLogin, Long> {
    fun findByProviderAndProviderUserId(provider: SocialProvider, providerUserId: String): Mono<SocialLogin>
}
