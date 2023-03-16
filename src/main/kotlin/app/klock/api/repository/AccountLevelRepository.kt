package app.klock.api.repository

import app.klock.api.domain.entity.AccountLevel

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountLevelRepository : ReactiveCrudRepository<AccountLevel, Long>
