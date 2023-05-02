package app.klock.api.functional.timer

import app.klock.api.service.TimerService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class TimerHandler(private val timerService: TimerService) {

  fun getAllTimersByUserId(request: ServerRequest): Mono<ServerResponse> {
    return request.principal().flatMap { principal ->
      val userId = principal.name.toLong()
      timerService.getAllTimersByUserId(userId).collectList().flatMap { timers ->
        ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(timers)
      }
    }.switchIfEmpty(ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("Unauthorized"))
  }
  
}
