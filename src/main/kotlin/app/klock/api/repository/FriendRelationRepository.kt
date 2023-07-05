package app.klock.api.repository

import app.klock.api.domain.entity.FriendRelation
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface FriendRelationRepository : ReactiveCrudRepository<FriendRelation, Long> {
    fun findByUserIdAndFollowed(userId: Long, followed: Boolean): Flux<FriendRelation>
    fun findByUserIdAndFollowId(userId: Long, followId: Long): Mono<FriendRelation>

    fun deleteByUserIdAndFollowId(userId: Long, followId: Long) : Mono<Void>
}
