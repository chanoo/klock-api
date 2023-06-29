package app.klock.api.repository

import app.klock.api.domain.entity.FriendRelation
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface FriendRelationRepository : ReactiveCrudRepository<FriendRelation, Long> {
    fun findByUserIdAndLike(userId: Long, like: Boolean): Flux<FriendRelation>
}
