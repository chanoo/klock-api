package app.klock.api.functional.timer

import app.klock.api.service.TimerFocusService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class TimerFocusHandler(private val timerFocusService: TimerFocusService) {

  // Focus timer 생성
  suspend fun createFocusTimer(request: ServerRequest): ServerResponse {
    val timerDto = request.awaitBody<TimerFocusDto>()
    timerDto.validate()
    val timer = timerDto.toDomain()

    val createdTimer = timerFocusService.create(timer)
    val createdTimerDto = TimerFocusDto.from(createdTimer)

    return ServerResponse.status(HttpStatus.CREATED).bodyValueAndAwait(createdTimerDto)
  }

  // Focus timer 수정
  suspend fun updateFocusTimer(request: ServerRequest): ServerResponse {
    val timerId = request.pathVariable("id").toLong()
    val timerDto = request.awaitBody<TimerFocusDto>()
    timerDto.validate()

    val existingTimer = timerFocusService.get(timerId)

    return if (existingTimer != null) {
      val timer = timerDto.toDomain().copy(
        id = timerId
      )
      val updatedTimer = timerFocusService.update(timer)

      val updatedTimerDto = TimerFocusDto.from(updatedTimer)
      ServerResponse.ok().bodyValueAndAwait(updatedTimerDto)
    } else {
      ServerResponse.status(HttpStatus.NOT_FOUND).bodyValueAndAwait("Focus timer not found")
    }
  }

  // Focus timer 삭제
  suspend fun deleteFocusTimer(request: ServerRequest): ServerResponse {
    val timerId = request.pathVariable("id").toLong()
    val isDeleted = timerFocusService.delete(timerId)

    return if (isDeleted) {
      ServerResponse.status(HttpStatus.NO_CONTENT).buildAndAwait()
    } else {
      ServerResponse.status(HttpStatus.NOT_FOUND).bodyValueAndAwait("Focus timer not found")
    }
  }
}
