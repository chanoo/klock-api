package app.klock.api.repository

import app.klock.api.domain.entity.UserTimer
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserTimerRepository : ReactiveCrudRepository<UserTimer, Long>
