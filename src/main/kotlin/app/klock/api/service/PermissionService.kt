package app.klock.api.service

import app.klock.api.repository.TimerExamRepository
import app.klock.api.repository.TimerFocusRepository
import app.klock.api.repository.TimerPomodoroRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class PermissionService(
  private val timerFocusRepository: TimerFocusRepository,
  private val timerPomodoroRepository: TimerPomodoroRepository,
  private val timerExamRepository: TimerExamRepository
) {
  fun hasTimerFocusPermission(id: Long, userId: Long): Mono<Boolean> {
    return timerFocusRepository.findById(id)
      .map { it.userId == userId }
      .defaultIfEmpty(false)
  }

  fun hasTimerPomodoroPermission(id: Long, userId: Long): Mono<Boolean> {
    return timerPomodoroRepository.findById(id)
      .map { it.userId == userId }
      .defaultIfEmpty(false)
  }

  fun hasTimerExamPermission(id: Long, userId: Long): Mono<Boolean> {
    return timerExamRepository.findById(id)
      .map { it.userId == userId }
      .defaultIfEmpty(false)
  }
}