package app.klock.api.service

import app.klock.api.domain.entity.TimerPomodoro
import app.klock.api.repository.TimerPomodoroRepository
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TimerPomodoroService(
  private val timerPomodoroRepository: TimerPomodoroRepository
) {
  // Create TimerPomodoro
  fun create(timerPomodoro: TimerPomodoro): Mono<TimerPomodoro> = timerPomodoroRepository.save(timerPomodoro)

  // Read TimerPomodoro by id
  @PreAuthorize("@permissionService.hasTimerPomodoroPermission(#id, principal)")
  fun get(id: Long): Mono<TimerPomodoro> = timerPomodoroRepository.findById(id)

  // Update TimerPomodoro
  @PreAuthorize("@permissionService.hasTimerPomodoroPermission(#timerPomodoro.id, principal)")
  fun update(timerPomodoro: TimerPomodoro): Mono<TimerPomodoro> =
    timerPomodoroRepository.save(timerPomodoro)

  // Delete TimerPomodoro by id
  @PreAuthorize("@permissionService.hasTimerPomodoroPermission(#id, principal)")
  fun delete(id: Long): Mono<Boolean> {
    return timerPomodoroRepository.deleteById(id)
      .then(timerPomodoroRepository.findById(id).hasElement().map { !it })
  }
}
