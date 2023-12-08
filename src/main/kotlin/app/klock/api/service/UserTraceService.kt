package app.klock.api.service

import app.klock.api.domain.entity.FriendRelation
import app.klock.api.domain.entity.UserTrace
import app.klock.api.functional.friendRelation.FriendDetailDto
import app.klock.api.functional.friendRelation.FriendRelationDto
import app.klock.api.functional.userTrace.CreateUserTraceContent
import app.klock.api.repository.FriendRelationNativeSqlRepository
import app.klock.api.repository.FriendRelationRepository
import app.klock.api.repository.UserRepository
import app.klock.api.repository.UserTraceRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class UserTraceService(
    private val userTraceRepository: UserTraceRepository,
    private val userRepository: UserRepository) {

    fun createContent(userId: Long, userTrace: CreateUserTraceContent): Mono<UserTrace> {
        return userRepository.findById(userTrace.friendId)
            .flatMap { friend ->
                userTraceRepository.save(
                    UserTrace(
                        userId = userId,
                        friendId = userTrace.friendId,
                        friendNickName = friend.nickname,
                        contents = userTrace.contents,
                        createdAt = LocalDateTime.now()))
            }
    }

    fun getTraces(userId: Long): Mono<UserTrace> {
        return userTraceRepository.findByUserId(userId)
    }
}
