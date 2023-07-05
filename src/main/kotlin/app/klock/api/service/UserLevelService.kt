package app.klock.api.service

import app.klock.api.domain.entity.UserLevel
import app.klock.api.repository.UserLevelRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class UserLevelService(private val userLevelRepository: UserLevelRepository) {

  fun findByUserId(userId: Long): Mono<UserLevel> {
    return userLevelRepository.findByUserId(userId)
  }

  @Transactional
  fun create(userLevel: UserLevel): Mono<UserLevel> {
    return userLevelRepository.save(userLevel)
  }
}
