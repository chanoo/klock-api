package app.klock.api.repository

import app.klock.api.domain.entity.SocialLogin
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SocialLoginRepository : ReactiveCrudRepository<SocialLogin, Long>
