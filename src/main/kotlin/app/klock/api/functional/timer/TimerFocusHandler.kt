package app.klock.api.functional.timer

import TimerExamDto
import app.klock.api.service.TimerExamService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class TimerFocusHandler(private val timerExamService: TimerExamService) {

  // Focus timer 생성
  suspend fun createFocusTimer(request: ServerRequest): ServerResponse {
    val timerDto = request.awaitBody<TimerExamDto>()
    val timer = timerDto.toDomain()

    val createdTimer = timerExamService.create(timer)
    val createdTimerDto = TimerExamDto.from(createdTimer)

    return ServerResponse.status(HttpStatus.CREATED).bodyValueAndAwait(createdTimerDto)
  }

  // Focus timer 수정
  suspend fun updateFocusTimer(request: ServerRequest): ServerResponse {
    val timerId = request.pathVariable("id").toLong()
    val timerDto = request.awaitBody<TimerExamDto>()

    val timer = timerDto.toDomain().copy(id = timerId)
    val updatedTimer = timerExamService.update(timer)

    val updatedTimerDto = TimerExamDto.from(updatedTimer)
    return ServerResponse.ok().bodyValueAndAwait(updatedTimerDto)
  }

  // Focus timer 삭제
  suspend fun deleteFocusTimer(request: ServerRequest): ServerResponse {
    val timerId = request.pathVariable("id").toLong()
    val isDeleted = timerExamService.delete(timerId)

    return if (isDeleted) {
      ServerResponse.status(HttpStatus.NO_CONTENT).buildAndAwait()
    } else {
      ServerResponse.status(HttpStatus.NOT_FOUND).bodyValueAndAwait("Focus timer not found")
    }
  }
}
