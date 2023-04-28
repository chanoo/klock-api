package app.klock.api.functional.timer

import app.klock.api.service.TimerService
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class TimerHandler(private val timerService: TimerService) {

  suspend fun getAllTimersByUserId(request: ServerRequest): ServerResponse {
    val userId = request.pathVariable("userId").toLong()
    val timers = timerService.getAllTimersByUserId(userId)
    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(timers)
  }
}
