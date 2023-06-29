package app.klock.api.service

import app.klock.api.domain.entity.FriendRelation
import app.klock.api.repository.FriendRelationRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class FriendRelationService(private val friendRelationRepository: FriendRelationRepository) {

    fun create(userId: Long, followId: Long): Mono<FriendRelation> {
        val friendRelation = FriendRelation(userId = userId, followId = followId)
        return friendRelationRepository.save(friendRelation)
    }

    fun getFriendRelations(userId: Long): Flux<FriendRelation> {
        return friendRelationRepository.findByUserIdAndLike(userId, true)
    }

    fun deleteById(id: Long): Mono<Void> {
        return friendRelationRepository.deleteById(id)
    }
}
