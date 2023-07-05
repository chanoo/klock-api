package app.klock.api.functional.userTag

import app.klock.api.domain.entity.UserTag
import app.klock.api.service.UserTagService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.util.*

@Component
class UserTagHandler(private val userTagService: UserTagService) {
  // userId를 사용하여 태그 가져오기
  fun getUserTag(request: ServerRequest): Mono<ServerResponse> {
    val userId = request.queryParam("userId").flatMap { it.toLongOrNull()?.let { id -> Optional.of(id) } }.orElse(null)
    return if (userId != null) {
      userTagService.findByUserId(userId)
        .flatMap { tag ->
          ServerResponse.ok().bodyValue(tag)
        }
    } else {
      ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("Invalid userId")
    }
  }

  fun create(request: ServerRequest): Mono<ServerResponse> {
    return request.bodyToMono(UserTag::class.java)
      .flatMap { userTag -> userTagService.create(userTag) }
      .flatMap { createdUserTag -> ServerResponse.status(HttpStatus.CREATED).bodyValue(createdUserTag) }
  }
}
