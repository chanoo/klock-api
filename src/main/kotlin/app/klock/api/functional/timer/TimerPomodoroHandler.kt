package app.klock.api.functional.timer

import app.klock.api.service.TimerPomodoroService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono

@Component
class TimerPomodoroHandler(private val timerPomodoroService: TimerPomodoroService) {

  // Pomodoro timer 생성
  fun createPomodoroTimer(request: ServerRequest): Mono<ServerResponse> {
    return request.bodyToMono<TimerPomodoroDto>().flatMap { timerDto ->
      val timer = timerDto.toDomain()
      timer.validate()

      timerPomodoroService.create(timer).flatMap { createdTimer ->
        val createdTimerDto = TimerPomodoroDto.from(createdTimer)
        ServerResponse.status(HttpStatus.CREATED).bodyValue(createdTimerDto)
      }
    }
  }

  // Pomodoro timer 수정
  fun updatePomodoroTimer(request: ServerRequest): Mono<ServerResponse> {
    val timerId = request.pathVariable("id").toLong()

    return timerPomodoroService.get(timerId).flatMap { existingTimer ->
      request.bodyToMono<TimerPomodoroDto>().flatMap { timerDto ->
        val timer = timerDto.toDomain().copy(
          id = timerId
        )
        timer.validate()

        timerPomodoroService.update(timer).flatMap { updatedTimer ->
          val updatedTimerDto = TimerPomodoroDto.from(updatedTimer)
          ServerResponse.ok().bodyValue(updatedTimerDto)
        }
      }
    }.switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("Pomodoro timer not found"))
  }

  // Pomodoro timer 삭제
  fun deletePomodoroTimer(request: ServerRequest): Mono<ServerResponse> {
    val timerId = request.pathVariable("id").toLong()

    return timerPomodoroService.delete(timerId).flatMap { isDeleted ->
      if (isDeleted) {
        ServerResponse.status(HttpStatus.NO_CONTENT).build()
      } else {
        ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("Pomodoro timer not found")
      }
    }
  }
}
