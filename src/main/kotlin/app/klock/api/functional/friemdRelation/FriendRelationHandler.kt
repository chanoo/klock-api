package app.klock.api.functional.friendrelation

import app.klock.api.functional.friemdRelation.FriendRelationRequest
import app.klock.api.service.FriendRelationService
import app.klock.api.utils.JwtUtils
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.net.URI

@Component
class FriendRelationHandler(
  private val friendRelationService: FriendRelationService,
  private val jwtUtils: JwtUtils
) {

  fun create(request: ServerRequest): Mono<ServerResponse> {
    return request.bodyToMono(FriendRelationRequest::class.java)
      .flatMap { friendRelationRequest ->
        friendRelationService.create(
          userId = jwtUtils.getUserIdFromToken(),
          followId = friendRelationRequest.followId
        )
      }
      .flatMap { friendRelation ->
        ServerResponse.created(URI.create("/api/friendrelation/${friendRelation.id}"))
          .bodyValue(friendRelation)
      }
      .switchIfEmpty(ServerResponse.status(HttpStatus.BAD_REQUEST).build())
  }

  fun delete(request: ServerRequest): Mono<ServerResponse> {
    val id = request.pathVariable("id").toLong()
    return friendRelationService.deleteById(id)
      .then(ServerResponse.noContent().build())
      .switchIfEmpty(ServerResponse.notFound().build())
  }

  fun getFriendRelations(request: ServerRequest): Mono<ServerResponse> {
    return friendRelationService.getFriendRelations(jwtUtils.getUserIdFromToken())
      .collectList()
      .flatMap { friendRelations ->
        ServerResponse.ok().bodyValue(friendRelations)
      }
  }
}
