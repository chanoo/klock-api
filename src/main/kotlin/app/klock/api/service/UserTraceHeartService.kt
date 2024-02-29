package app.klock.api.service

import app.klock.api.domain.entity.UserTraceHeart
import app.klock.api.repository.UserTraceHeartRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserTraceHeartService(
    private val userTraceHeartRepository: UserTraceHeartRepository
) {

  private fun createUserTraceHeart(userId: Long, userTraceId: Long): Mono<UserTraceHeart> {
    val heartToSave = UserTraceHeart(
        userTraceId = userTraceId,
        userId = userId,
        heartCount = 1
    )
    return userTraceHeartRepository.save(heartToSave)
  }

  fun updateHeart(traceId: Long, userId: Long): Mono<UserTraceHeart> {
    return userTraceHeartRepository.findByUserTraceIdAndUserId(traceId, userId)
        .flatMap { heart ->
            userTraceHeartRepository.save(
                heart.copy(
                    heartCount = heart.heartCount + 1
                )
            )
        }
        .switchIfEmpty(
            createUserTraceHeart(userId, traceId)
        )
  }

    fun cancelHeart(traceId: Long, userId: Long): Mono<UserTraceHeart> {
        return userTraceHeartRepository.findByUserTraceIdAndUserId(traceId, userId)
            .flatMap { heart ->
                if (heart.heartCount > 0) {
                    userTraceHeartRepository.save(
                        heart.copy(
                            heartCount = heart.heartCount - 1
                        )
                    )
                } else {
                    Mono.just(heart)
                }
            }
    }
}