package app.klock.api.service

import app.klock.api.domain.entity.TimerExam
import app.klock.api.domain.entity.TimerFocus
import app.klock.api.domain.entity.TimerPomodoro
import app.klock.api.functional.timer.*
import app.klock.api.repository.TimerExamRepository
import app.klock.api.repository.TimerFocusRepository
import app.klock.api.repository.TimerPomodoroRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TimerService(
  private val timerExamRepository: TimerExamRepository,
  private val timerPomodoroRepository: TimerPomodoroRepository,
  private val timerFocusRepository: TimerFocusRepository
) {
  fun getAllTimersByUserId(userId: Long): Flux<TimerDto> {
    val timerExams = timerExamRepository.findAllByUserIdOrderBySeq(userId)
      .map {
        TimerExamDto(
          it.id!!,
          it.userId,
          it.seq,
          "exam",
          it.name,
          it.startTime,
          it.duration,
          it.questionCount
        )
      }

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
      .map {
        TimerFocusDto(
          it.id!!,
          it.userId,
          it.seq,
          "focus",
          it.name
        )
      }

    return Flux
      .concat(timerExams, timerPomodoros, timerStudies)
      .sort(compareBy {
        it.seq
      })
  }

  fun updateTimersSeq(timerSeqArray: Array<TimerSeqDto>): Mono<Boolean> {
    timerSeqArray.iterator().forEach { timerSeq ->
      val result = when (timerSeq.type) {
        TimerType.FOCUS -> updateFocus(timerSeq)
        TimerType.EXAM -> updateExam(timerSeq)
        TimerType.POMODORO -> updatePomodoro(timerSeq)
      }
    }
    return Mono.just(true)
  }

  private fun updateFocus(timerSeq: TimerSeqDto): Mono<TimerFocus> {
    return timerFocusRepository.findById(timerSeq.id)
      .switchIfEmpty(Mono.empty())
      .filter { existingTimer ->
      timerSeq.seq != existingTimer.seq
    }.flatMap { existingTimer ->
      val timer = existingTimer.copy(
        seq = timerSeq.seq
      )
      timer.validate()
      timerFocusRepository.save(timer)
    }
  }

  private fun updateExam(timerSeq: TimerSeqDto): Mono<TimerExam> {
    return timerExamRepository.findById(timerSeq.id)
      .switchIfEmpty(Mono.empty())
      .filter { existingTimer ->
      timerSeq.seq != existingTimer.seq
    }.flatMap { existingTimer ->
      val timer = existingTimer.copy(
        seq = timerSeq.seq
      )
      timer.validate()
      timerExamRepository.save(timer)
    }
  }

  private fun updatePomodoro(timerSeq: TimerSeqDto): Mono<TimerPomodoro> {
    return timerPomodoroRepository.findById(timerSeq.id)
      .switchIfEmpty(Mono.empty())
      .filter { existingTimer ->
      timerSeq.seq != existingTimer.seq
    }.flatMap { existingTimer ->
      val timer = existingTimer.copy(
        seq = timerSeq.seq
      )
      timer.validate()
      timerPomodoroRepository.save(timer)
    }
  }
}
