package app.klock.api.functional.timer

import app.klock.api.service.TimerFocusService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Component
class TimerFocusHandler(private val timerFocusService: TimerFocusService) {

  // Focus timer 생성
  fun createFocusTimer(request: ServerRequest): Mono<ServerResponse> {
    return request.principal().flatMap { principal ->
      val userId = principal.name.toLong()
      request.bodyToMono<TimerFocusDto>().flatMap { timerDto ->
        val timer = timerDto.toDomain().copy(
          userId = userId
        )
        timer.validate()

        timerFocusService.create(timer).flatMap { createdTimer ->
          val createdTimerDto = TimerFocusDto.from(createdTimer)
          ServerResponse.status(HttpStatus.CREATED).bodyValue(createdTimerDto)
        }
      }
    }.switchIfEmpty(ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("Unauthorized"))
  }

  // Focus timer 수정
  fun updateFocusTimer(request: ServerRequest): Mono<ServerResponse> {
    val timerId = request.pathVariable("id").toLong()

    return timerFocusService.get(timerId).flatMap { existingTimer ->
      request.bodyToMono<TimerFocusDto>().flatMap { timerDto ->
        val timer = existingTimer.copy(
          name = timerDto.name,
          updatedAt = LocalDateTime.now()
        )
        timer.validate()
        timerFocusService.update(timer).flatMap { updatedTimer ->
          val updatedTimerDto = TimerFocusDto.from(updatedTimer)
          ServerResponse.ok().bodyValue(updatedTimerDto)
        }
      }
    }.switchIfEmpty(ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("Unauthorized"))
  }

  // Focus timer 삭제
  fun deleteFocusTimer(request: ServerRequest): Mono<ServerResponse> {
    val timerId = request.pathVariable("id").toLong()

    return timerFocusService.delete(timerId).flatMap { isDeleted ->
      if (isDeleted) {
        ServerResponse.status(HttpStatus.NO_CONTENT).build()
      } else {
        ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("Focus timer not found")
      }
    }.switchIfEmpty(ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("Unauthorized"))
  }
}
