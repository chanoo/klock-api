package app.klock.api.service

import app.klock.api.domain.entity.TimerExam
import app.klock.api.repository.TimerExamRepository
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TimerExamService(
  private val timerExamRepository: TimerExamRepository
) {
  // Create TimerExam
  fun create(timerExam: TimerExam): Mono<TimerExam> = timerExamRepository.save(timerExam)

  // Read TimerExam by id
  @PreAuthorize("@permissionService.hasTimerExamPermission(#id, principal)")
  fun get(id: Long): Mono<TimerExam> = timerExamRepository.findById(id)

  // Update TimerExam
  @PreAuthorize("@permissionService.hasTimerFocusPermission(#timerExam.id, principal)")
  fun update(timerExam: TimerExam): Mono<TimerExam> = timerExamRepository.save(timerExam)

  // Delete TimerExam by id
  @PreAuthorize("@permissionService.hasTimerExamPermission(#id, principal)")
  fun delete(id: Long): Mono<Boolean> {
    return timerExamRepository.deleteById(id)
      .then(timerExamRepository.findById(id).hasElement().map { !it })
  }
}
