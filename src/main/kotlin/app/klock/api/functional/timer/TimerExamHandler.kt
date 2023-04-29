package app.klock.api.functional.timer

import app.klock.api.service.TimerExamService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class TimerExamHandler(private val timerExamService: TimerExamService) {

  // 시험시간 타이머 생성
  suspend fun createExamTimer(request: ServerRequest): ServerResponse {
    val timerDto = request.awaitBody<TimerExamDto>()
    timerDto.validate()
    val timer = timerDto.toDomain()

    val createdTimer = timerExamService.create(timer)
    val createdTimerDto = TimerExamDto.from(createdTimer)

    return ServerResponse.status(HttpStatus.CREATED).bodyValueAndAwait(createdTimerDto)
  }

  // 시험시간 타이머 수정
  suspend fun updateExamTimer(request: ServerRequest): ServerResponse {
    val timerId = request.pathVariable("id").toLong()
    val timerDto = request.awaitBody<TimerExamDto>()
    timerDto.validate()

    val existingTimer = timerExamService.get(timerId)

    return if (existingTimer != null) {
      val timer = timerDto.toDomain().copy(
        id = timerId
      )
      val updatedTimer = timerExamService.update(timer)

      val updatedTimerDto = TimerExamDto.from(updatedTimer)
      ServerResponse.ok().bodyValueAndAwait(updatedTimerDto)
    } else {
      ServerResponse.status(HttpStatus.NOT_FOUND).bodyValueAndAwait("Exam timer not found")
    }
  }

  // 시험시간 타이머 삭제
  suspend fun deleteExamTimer(request: ServerRequest): ServerResponse {
    val timerId = request.pathVariable("id").toLong()
    val isDeleted = timerExamService.delete(timerId)

    return if (isDeleted) {
      ServerResponse.status(HttpStatus.NO_CONTENT).buildAndAwait()
    } else {
      ServerResponse.status(HttpStatus.NOT_FOUND).bodyValueAndAwait("Exam timer not found")
    }
  }
}
