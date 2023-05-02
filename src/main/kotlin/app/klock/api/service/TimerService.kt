package app.klock.api.service

import app.klock.api.functional.timer.TimerDto
import app.klock.api.functional.timer.TimerExamDto
import app.klock.api.functional.timer.TimerFocusDto
import app.klock.api.functional.timer.TimerPomodoroDto
import app.klock.api.repository.TimerExamRepository
import app.klock.api.repository.TimerFocusRepository
import app.klock.api.repository.TimerPomodoroRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class TimerService(
  private val timerExamRepository: TimerExamRepository,
  private val timerPomodoroRepository: TimerPomodoroRepository,
  private val timerFocusRepository: TimerFocusRepository
) {
  fun getAllTimersByUserId(userId: Long): Flux<TimerDto> {
    val timerExams = timerExamRepository.findAllByUserIdOrderBySeq(userId)
      .map { TimerExamDto(it.id!!, it.userId, it.seq, "exam", it.name, it.startTime, it.duration, it.questionCount) }

    val timerPomodoros = timerPomodoroRepository.findAllByUserIdOrderBySeq(userId)
      .map {
        TimerPomodoroDto(
          it.id!!,
          it.userId,
          it.seq,
          "pomodoro",
          it.name,
          it.focusTime,
          it.restTime,
          it.cycleCount
        )
      }

    val timerStudies = timerFocusRepository.findAllByUserIdOrderBySeq(userId)
      .map { TimerFocusDto(it.id!!, it.userId, it.seq, "focus", it.name) }

    return Flux.concat(timerExams, timerPomodoros, timerStudies).sort(compareBy { it.seq })
  }
}
