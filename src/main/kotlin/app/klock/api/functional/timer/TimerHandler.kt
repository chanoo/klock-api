package app.klock.api.functional.timer

import app.klock.api.service.TimerService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
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

  // 타이머 seq 순서 업데이트
  fun updateTimersSeq(request: ServerRequest) : Mono<ServerResponse> {
    return request.bodyToMono<String>().flatMap { jsonArrayString ->
      val objectMapper = ObjectMapper()
      val timerSeqArray = objectMapper.readValue(jsonArrayString, Array<TimerSeqDto>::class.java)
      timerService.updateTimersSeq(
        timerSeqArray = timerSeqArray
      ).flatMap {
        ServerResponse.status(HttpStatus.OK).build()
      }
    }
  }
  
}
