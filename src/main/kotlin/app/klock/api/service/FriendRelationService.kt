package app.klock.api.service

import app.klock.api.domain.entity.FriendRelation
import app.klock.api.functional.friendRelation.FriendDetailDto
import app.klock.api.functional.friendRelation.FriendRelationDto
import app.klock.api.repository.FriendRelationNativeSqlRepository
import app.klock.api.repository.FriendRelationRepository
import app.klock.api.repository.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class FriendRelationService(
    private val friendRelationRepository: FriendRelationRepository,
    private val friendRelationNativeSqlRepository: FriendRelationNativeSqlRepository,
    private val userRepository: UserRepository) {

    fun create(userId: Long, followId: Long): Mono<FriendRelationDto> {
        return friendRelationRepository.findByUserIdAndFollowId(followId, userId)
            .flatMap { existingRelation ->
                val updated = existingRelation.copy(followed = true)
                friendRelationRepository.save(updated)
                     .then(friendRelationRepository.save(FriendRelation(userId = userId, followId = followId, followed = true)))
            }
            .switchIfEmpty(
                friendRelationRepository.save(FriendRelation(userId = userId, followId = followId, followed = false))
            )
            .flatMap { friendRelation ->
                userRepository.findById(friendRelation.followId)
                    .map { FriendRelationDto.from(friendRelation, it) }
            }
    }

    fun unfollow(userId: Long, followId: Long): Mono<Void> {
        //맞팔여부 체크
        friendRelationRepository.findByUserIdAndFollowId(followId, userId)
            .flatMap { existingRelation ->
                val updated = existingRelation.copy(followed = false)
                friendRelationRepository.save(updated)
            }

        return friendRelationRepository.deleteByUserIdAndFollowId(userId, followId)
    }

    fun getFriendRelations(userId: Long): Flux<FriendDetailDto> {
        return friendRelationNativeSqlRepository.findFriendDetails(userId)
    }

    /**
     * QR코드를 통해 팔로우(맞팔) 처리
     */
    fun followFromQrCode(userId: Long, followId: Long): Mono<FriendRelationDto> {
        return friendRelationRepository.findByUserIdAndFollowId(followId, userId)
            .flatMap { existingRelation ->
                val updated = existingRelation.copy(followed = true)
                friendRelationRepository.save(updated)
            }
            .switchIfEmpty(
                friendRelationRepository.save(FriendRelation(userId = followId, followId = userId, followed = true))
            )
            .then(
                friendRelationRepository.save(FriendRelation(userId = userId, followId = followId, followed = true))
            )
            .flatMap { friendRelation ->
                userRepository.findById(friendRelation.followId)
                    .map { FriendRelationDto.from(friendRelation, it) }
            }
    }
}

