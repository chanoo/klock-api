package app.klock.api.service

import app.klock.api.domain.entity.UserTag
import app.klock.api.repository.UserTagRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class UserTagService(private val userTagRepository: UserTagRepository) {

  fun findByUserId(userId: Long): Mono<UserTag> {
    return userTagRepository.findByUserId(userId)
  }

  fun create(userTag: UserTag): Mono<UserTag> {
    return userTagRepository.save(userTag)
  }
}
