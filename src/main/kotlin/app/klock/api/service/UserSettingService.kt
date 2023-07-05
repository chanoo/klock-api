package app.klock.api.service

import app.klock.api.domain.entity.UserSetting
import app.klock.api.repository.UserSettingRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class UserSettingService(private val userSettingRepository: UserSettingRepository) {

  fun findByUserId(userId: Long): Mono<UserSetting> {
    return userSettingRepository.findByUserId(userId)
  }

  fun create(userSetting: UserSetting): Mono<UserSetting> {
    return userSettingRepository.save(userSetting)
  }
}
