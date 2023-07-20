package app.klock.api.functional.timer

import app.klock.api.service.TimerAutoService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Component
class TimerAutoHandler(private val timerAutoService: TimerAutoService) {

  // Auto timer 생성
  fun createAutoTimer(request: ServerRequest): Mono<ServerResponse> {
    return request.principal().flatMap { principal ->
      val userId = principal.name.toLong()
      request.bodyToMono<TimerAutoDto>().flatMap { timerDto ->
        val timer = timerDto.toDomain().copy(
          userId = userId
        )
        timer.validate()

        timerAutoService.create(timer).flatMap { createdTimer ->
          val createdTimerDto = TimerAutoDto.from(createdTimer)
          ServerResponse.status(HttpStatus.CREATED).bodyValue(createdTimerDto)
        }
      }
    }.switchIfEmpty(ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("Unauthorized"))
  }

  // Auto timer 수정
  fun updateAutoTimer(request: ServerRequest): Mono<ServerResponse> {
    val timerId = request.pathVariable("id").toLong()

    return timerAutoService.get(timerId).flatMap { existingTimer ->
      request.bodyToMono<TimerAutoDto>().flatMap { timerDto ->
        val timer = existingTimer.copy(
          name = timerDto.name,
          updatedAt = LocalDateTime.now()
        )
        timer.validate()
        timerAutoService.update(timer).flatMap { updatedTimer ->
          val updatedTimerDto = TimerAutoDto.from(updatedTimer)
          ServerResponse.ok().bodyValue(updatedTimerDto)
        }
      }
    }.switchIfEmpty(ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("Unauthorized"))
  }

  // Auto timer 삭제
  fun deleteAutoTimer(request: ServerRequest): Mono<ServerResponse> {
    val timerId = request.pathVariable("id").toLong()

    return timerAutoService.delete(timerId).flatMap { isDeleted ->
      if (isDeleted) {
        ServerResponse.status(HttpStatus.NO_CONTENT).build()
      } else {
        ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("Focus timer not found")
      }
    }.switchIfEmpty(ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("Unauthorized"))
  }
}
