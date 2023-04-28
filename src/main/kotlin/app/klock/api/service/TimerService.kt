package app.klock.api.service

import TimerDto
import TimerExamDto
import TimerFocusDto
import TimerPomodoroDto
import app.klock.api.repository.TimerExamRepository
import app.klock.api.repository.TimerFocusRepository
import app.klock.api.repository.TimerPomodoroRepository
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service

@Service
class TimerService(
  private val timerExamRepository: TimerExamRepository,
  private val timerPomodoroRepository: TimerPomodoroRepository,
  private val timerFocusRepository: TimerFocusRepository
) {
  suspend fun getAllTimersByUserId(userId: Long): List<TimerDto> {
    val timerExams = timerExamRepository.findAllByUserIdOrderBySeq(userId)
      .map { TimerExamDto(it.id!!, it.userId, it.seq, it.name, it.startTime, it.duration, it.questionCount) }
      .collectList()
      .awaitSingle()

    val timerPomodoros = timerPomodoroRepository.findAllByUserIdOrderBySeq(userId)
      .map { TimerPomodoroDto(it.id!!, it.userId, it.seq, it.name, it.focusTime, it.restTime, it.cycleCount) }
      .collectList()
      .awaitSingle()

    val timerStudies = timerFocusRepository.findAllByUserIdOrderBySeq(userId)
      .map { TimerFocusDto(it.id!!, it.userId, it.seq, it.name) }
      .collectList()
      .awaitSingle()

    return (timerExams + timerPomodoros + timerStudies).sortedBy { it.seq }
  }

}
