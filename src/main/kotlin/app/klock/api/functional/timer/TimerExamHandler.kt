package app.klock.api.functional.timer

import app.klock.api.service.TimerExamService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono

@Component
class TimerExamHandler(private val timerExamService: TimerExamService) {

  // 시험시간 타이머 생성
  fun createExamTimer(request: ServerRequest): Mono<ServerResponse> {
    return request.bodyToMono<TimerExamDto>().flatMap { timerDto ->
      val timer = timerDto.toDomain()
      timer.validate()

      timerExamService.create(timer).flatMap { createdTimer ->
        val createdTimerDto = TimerExamDto.from(createdTimer)
        ServerResponse.status(HttpStatus.CREATED).bodyValue(createdTimerDto)
      }
    }
  }

  // 시험시간 타이머 수정
  fun updateExamTimer(request: ServerRequest): Mono<ServerResponse> {
    val timerId = request.pathVariable("id").toLong()

    return timerExamService.get(timerId).flatMap { existingTimer ->
      request.bodyToMono<TimerExamDto>().flatMap { timerDto ->
        val timer = timerDto.toDomain().copy(
          id = timerId
        )
        timer.validate()
        timerExamService.update(timer).flatMap { updatedTimer ->
          val updatedTimerDto = TimerExamDto.from(updatedTimer)
          ServerResponse.ok().bodyValue(updatedTimerDto)
        }
      }
    }.switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("Exam timer not found"))
  }

  // 시험시간 타이머 삭제
  fun deleteExamTimer(request: ServerRequest): Mono<ServerResponse> {
    val timerId = request.pathVariable("id").toLong()

    return timerExamService.delete(timerId).flatMap { isDeleted ->
      if (isDeleted) {
        ServerResponse.status(HttpStatus.NO_CONTENT).build()
      } else {
        ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("Exam timer not found")
      }
    }.switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("Exam timer not found"))
  }
}
