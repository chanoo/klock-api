package app.klock.api.functional.friendrelation

import app.klock.api.functional.friemdRelation.FriendRelationRequest
import app.klock.api.service.FriendRelationService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.net.URI

@Component
class FriendRelationHandler(
    private val friendRelationService: FriendRelationService
) {

    fun createFriendRelation(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(FriendRelationRequest::class.java)
            .flatMap { friendRelationRequest ->
                friendRelationService.create(
                    requesterId = friendRelationRequest.requesterId,
                    friendId = friendRelationRequest.friendId
                )
            }
            .flatMap { friendRelation ->
                ServerResponse.created(URI.create("/api/friendrelation/${friendRelation.id}"))
                    .bodyValue(friendRelation)
            }
            .switchIfEmpty(ServerResponse.status(HttpStatus.BAD_REQUEST).build())
    }

    fun getFriendRelationsByRequesterId(request: ServerRequest): Mono<ServerResponse> {
        val requesterId = request.queryParam("requesterId").orElse(null)?.toLongOrNull()
        return if (requesterId != null) {
            friendRelationService.getFriendRelationsByRequesterId(requesterId)
                .collectList()
                .flatMap { friendRelations ->
                    ServerResponse.ok().bodyValue(friendRelations)
                }
        } else {
            ServerResponse.badRequest().build()
        }
    }
}
