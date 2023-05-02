package app.klock.api.functional.timer

import app.klock.api.service.TimerPomodoroService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class TimerPomodoroHandler(private val timerPomodoroService: TimerPomodoroService) {

  // Pomodoro timer 생성
  suspend fun createPomodoroTimer(request: ServerRequest): ServerResponse {
    val timerDto = request.awaitBody<TimerPomodoroDto>()
    val timer = timerDto.toDomain()
    timer.validate()

    val createdTimer = timerPomodoroService.create(timer)
    val createdTimerDto = TimerPomodoroDto.from(createdTimer)

    return ServerResponse.status(HttpStatus.CREATED).bodyValueAndAwait(createdTimerDto)
  }

  // Pomodoro timer 수정
  suspend fun updatePomodoroTimer(request: ServerRequest): ServerResponse {
    val timerId = request.pathVariable("id").toLong()
    val timerDto = request.awaitBody<TimerPomodoroDto>()

    val existingTimer = timerPomodoroService.get(timerId)

    return if (existingTimer != null) {
      val timer = timerDto.toDomain().copy(
        id = timerId
      )
      timer.validate()
      val updatedTimer = timerPomodoroService.update(timer)

      val updatedTimerDto = TimerPomodoroDto.from(updatedTimer)
      ServerResponse.ok().bodyValueAndAwait(updatedTimerDto)
    } else {
      ServerResponse.status(HttpStatus.NOT_FOUND).bodyValueAndAwait("Pomodoro timer not found")
    }
  }


  // Pomodoro timer 삭제
  suspend fun deletePomodoroTimer(request: ServerRequest): ServerResponse {
    val timerId = request.pathVariable("id").toLong()
    val isDeleted = timerPomodoroService.delete(timerId)

    return if (isDeleted) {
      ServerResponse.status(HttpStatus.NO_CONTENT).buildAndAwait()
    } else {
      ServerResponse.status(HttpStatus.NOT_FOUND).bodyValueAndAwait("Pomodoro timer not found")
    }
  }
}
