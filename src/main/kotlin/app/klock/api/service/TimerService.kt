package app.klock.api.service

import app.klock.api.domain.entity.TimerAuto
import app.klock.api.domain.entity.TimerExam
import app.klock.api.domain.entity.TimerFocus
import app.klock.api.domain.entity.TimerPomodoro
import app.klock.api.functional.timer.*
import app.klock.api.repository.TimerAutoRepository
import app.klock.api.repository.TimerExamRepository
import app.klock.api.repository.TimerFocusRepository
import app.klock.api.repository.TimerPomodoroRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class TimerService(
  private val timerExamRepository: TimerExamRepository,
  private val timerPomodoroRepository: TimerPomodoroRepository,
  private val timerFocusRepository: TimerFocusRepository,
  private val timerAutoRepository: TimerAutoRepository
) {
  fun getAllTimersByUserId(userId: Long): Flux<TimerDto> {
    val timerExams = timerExamRepository.findAllByUserIdOrderBySeq(userId)
      .map {
        TimerExamDto(
          it.id!!,
          it.userId,
          it.seq,
          TimerType.EXAM,
          it.name,
          it.startTime,
          it.duration,
          it.questionCount,
          it.markingTime
        )
      }

    val timerPomodoros = timerPomodoroRepository.findAllByUserIdOrderBySeq(userId)
      .map {
        TimerPomodoroDto(
          it.id!!,
          it.userId,
          it.seq,
          TimerType.POMODORO,
          it.name,
          it.focusTime,
          it.breakTime,
          it.cycleCount
        )
      }

    val timerStudies = timerFocusRepository.findAllByUserIdOrderBySeq(userId)
      .map {
        TimerFocusDto(
          it.id!!,
          it.userId,
          it.seq,
          TimerType.FOCUS,
          it.name
        )
      }

    val timerAutos = timerAutoRepository.findAllByUserIdOrderBySeq(userId)
      .map {
        TimerAutoDto(
          it.id!!,
          it.userId,
          it.seq,
          TimerType.AUTO,
          it.name
        )
      }

    return Flux
      .concat(timerExams, timerPomodoros, timerStudies, timerAutos)
      .sort(compareBy {
        it.seq
      })
  }

  fun updateTimersSeq(timerSeqArray: Array<TimerSeqDto>): Mono<Boolean> {
    timerSeqArray.iterator().forEach { timerSeq ->
      when (timerSeq.type) {
        TimerType.FOCUS -> updateFocus(timerSeq)
        TimerType.EXAM -> updateExam(timerSeq)
        TimerType.POMODORO -> updatePomodoro(timerSeq)
        TimerType.AUTO -> updateAuto(timerSeq)
        else -> {}
      }
    }
    return Mono.just(true)
  }

  fun updateFocus(timerSeq: TimerSeqDto): Mono<TimerFocus> {
    return timerFocusRepository.findById(timerSeq.id)
      .filter { existingTimer ->
        timerSeq.seq != existingTimer.seq
      }.flatMap { existingTimer ->
        val timer = existingTimer.copy(
          seq = timerSeq.seq,
          updatedAt = LocalDateTime.now()
        )
      timer.validate()
      timerFocusRepository.save(timer)
    }
  }

  fun updateExam(timerSeq: TimerSeqDto): Mono<TimerExam> {
    return timerExamRepository.findById(timerSeq.id)
      .filter { existingTimer ->
        timerSeq.seq != existingTimer.seq
      }.flatMap { existingTimer ->
        val timer = existingTimer.copy(
          seq = timerSeq.seq,
          updatedAt = LocalDateTime.now()
        )
      timer.validate()
      timerExamRepository.save(timer)
    }
  }

  fun updatePomodoro(timerSeq: TimerSeqDto): Mono<TimerPomodoro> {
    return timerPomodoroRepository.findById(timerSeq.id)
      .filter { existingTimer ->
        timerSeq.seq != existingTimer.seq
      }.flatMap { existingTimer ->
        val timer = existingTimer.copy(
          seq = timerSeq.seq,
          updatedAt = LocalDateTime.now()
        )
      timer.validate()
      timerPomodoroRepository.save(timer)
    }
  }

  fun updateAuto(timerSeq: TimerSeqDto): Mono<TimerAuto> {
    return timerAutoRepository.findById(timerSeq.id)
      .filter { existingTimer ->
        timerSeq.seq != existingTimer.seq
      }.flatMap { existingTimer ->
        val timer = existingTimer.copy(
          seq = timerSeq.seq,
          updatedAt = LocalDateTime.now()
        )
        timer.validate()
        timerAutoRepository.save(timer)
      }
  }
}
