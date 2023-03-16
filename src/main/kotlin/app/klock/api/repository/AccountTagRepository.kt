package app.klock.api.repository

import app.klock.api.domain.entity.AccountTag
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountTagRepository : ReactiveCrudRepository<AccountTag, Long>
