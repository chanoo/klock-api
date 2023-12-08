package app.klock.api.functional.friendRelation

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

  /**
   * 팔로우 요청
   */
  fun follow(request: ServerRequest): Mono<ServerResponse> {
    return jwtUtils.getUserIdFromToken()
      .flatMap { userId ->
        request.bodyToMono(FriendRelationRequest::class.java)
          .flatMap { friendRelationRequest ->
            friendRelationService.create(
              userId = userId,
              followId = friendRelationRequest.followId
            )
          }
          .flatMap { friendRelation ->
            ServerResponse.created(URI.create("/api/v1/friend-relations/${friendRelation.id}")).bodyValue(friendRelation)
          }
          .switchIfEmpty(ServerResponse.status(HttpStatus.BAD_REQUEST).build())
      }
  }

  /**
   * 유저가 팔로우한 유저를 다시 언팔로우
   * 팔로우한 유저를 찾지 못하면 404 에러 발생
   */
  fun unfollow(request: ServerRequest): Mono<ServerResponse> {
    return jwtUtils.getUserIdFromToken()
      .flatMap { userId ->
        request.bodyToMono(FriendRelationRequest::class.java)
          .flatMap { friendRelationRequest ->
            friendRelationService.unfollow(
              userId = userId,
              followId = friendRelationRequest.followId
            )
          }
          .then(ServerResponse.noContent().build())
          .switchIfEmpty(ServerResponse.notFound().build())
      }
  }

  fun getFriendRelations(request: ServerRequest): Mono<ServerResponse> {
    return jwtUtils.getUserIdFromToken()
      .flatMap { userId ->
        friendRelationService.getFriendRelations(userId)
          .collectList()
          .flatMap { friendRelations ->
            ServerResponse.ok().bodyValue(friendRelations)
          }
      }
  }
}
