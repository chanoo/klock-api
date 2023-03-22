package app.klock.api.service

import app.klock.api.domain.entity.FriendRelation
import app.klock.api.repository.FriendRelationRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class FriendRelationService(private val friendRelationRepository: FriendRelationRepository) {

    fun create(requesterId: Long, friendId: Long): Mono<FriendRelation> {
        val friendRelation = FriendRelation(requesterId = requesterId, friendId = friendId)
        return friendRelationRepository.save(friendRelation)
    }

    fun getFriendRelationsByRequesterId(requesterId: Long): Flux<FriendRelation> {
        return friendRelationRepository.findByRequesterId(requesterId)
    }
}
