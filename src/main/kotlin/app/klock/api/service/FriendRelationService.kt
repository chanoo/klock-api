package app.klock.api.service

import app.klock.api.domain.entity.FriendRelation
import app.klock.api.functional.friemdRelation.FriendRelationDto
import app.klock.api.functional.studySession.StudySessionDto
import app.klock.api.repository.FriendRelationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class FriendRelationService(private val friendRelationRepository: FriendRelationRepository) {

    @Transactional
    fun create(userId: Long, followId: Long): Mono<FriendRelationDto> {
        return friendRelationRepository.findByUserIdAndFollowId(followId, userId)
            .flatMap { existingRelation ->
                val updated = existingRelation.copy(followed = true)
                friendRelationRepository.save(updated)
                    .then(friendRelationRepository.save(FriendRelation(userId = followId, followId = userId, followed = true)))
            }
            .switchIfEmpty(
                friendRelationRepository.save(FriendRelation(userId = userId, followId = followId, followed = false))
            )
            .map { FriendRelationDto.from(it) }
    }

    @Transactional
    fun unfollow(userId: Long, followId: Long): Mono<Void> {
        //맞팔여부 체크
        friendRelationRepository.findByUserIdAndFollowId(followId, userId)
            .flatMap { existingRelation ->
                val updated = existingRelation.copy(followed = false)
                friendRelationRepository.save(updated)
            }

        return friendRelationRepository.deleteByUserIdAndFollowId(userId, followId)
    }

    fun getFriendRelations(userId: Long): Flux<FriendRelationDto> {
        return friendRelationRepository.findByUserIdAndFollowed(userId, true)
            .map { FriendRelationDto.from(it)
            }
    }
}
