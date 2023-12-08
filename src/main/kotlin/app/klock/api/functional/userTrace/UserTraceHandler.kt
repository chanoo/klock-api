package app.klock.api.functional.userTrace

import app.klock.api.domain.entity.UserTag
import app.klock.api.service.UserTraceService
import app.klock.api.utils.JwtUtils
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.util.*

@Component
class UserTraceHandler(private val userTraceService: UserTraceService, val jwtUtils: JwtUtils) {
  // 담벼락 리스트
  fun getUserTrace(request: ServerRequest): Mono<ServerResponse> {
      return jwtUtils.getUserIdFromToken()
        .flatMap { userId ->
          userTraceService.getTraces(userId)
            .flatMap { traces ->
              ServerResponse.ok().bodyValue(traces)
            }
        }
  }

  // 담벼락 컨텐츠 생성
  fun createContent(request: ServerRequest): Mono<ServerResponse> {
    return jwtUtils.getUserIdFromToken()
      .flatMap { userId ->
        request.bodyToMono(CreateUserTraceContent::class.java)
          .flatMap {
              content -> userTraceService.createContent(userId, content)
          }
          .flatMap {
            userTrace -> ServerResponse.status(HttpStatus.CREATED).bodyValue(userTrace) }
      }
  }
}
