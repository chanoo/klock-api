package app.klock.api.service

import app.klock.api.domain.entity.AccountTag
import app.klock.api.repository.AccountTagRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class AccountTagService(private val accountTagRepository: AccountTagRepository) {

    fun findByAccountId(accountId: Long): Flux<AccountTag> {
        return accountTagRepository.findByAccountId(accountId)
    }

    fun create(accountTag: AccountTag): Mono<AccountTag> {
        return accountTagRepository.save(accountTag)
    }
}
