package app.klock.api.repository

import app.klock.api.domain.entity.UserSetting
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserSettingRepository : ReactiveCrudRepository<UserSetting, Long> {
  fun findByUserId(userId: Long): Mono<UserSetting>
  fun deleteByUserId(userId: Long): Mono<Void>
}
