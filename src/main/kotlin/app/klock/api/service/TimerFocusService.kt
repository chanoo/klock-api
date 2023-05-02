package app.klock.api.service

import app.klock.api.domain.entity.TimerFocus
import app.klock.api.repository.TimerFocusRepository
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TimerFocusService(
  private val timerFocusRepository: TimerFocusRepository,
  private val permissionService: PermissionService
) {
  // Create TimerFocus
  fun create(timerFocus: TimerFocus): Mono<TimerFocus> = timerFocusRepository.save(timerFocus)

  // Read TimerFocus by id
  @PreAuthorize("@permissionService.hasTimerFocusPermission(#id, principal)")
  fun get(id: Long): Mono<TimerFocus> = timerFocusRepository.findById(id)

  // Update TimerFocus
  @PreAuthorize("@permissionService.hasTimerFocusPermission(#timerFocus.id, principal)")
  fun update(timerFocus: TimerFocus): Mono<TimerFocus> = timerFocusRepository.save(timerFocus)

  // Delete TimerFocus by id
  @PreAuthorize("@permissionService.hasTimerFocusPermission(#id, principal)")
  fun delete(id: Long): Mono<Boolean> {
    return timerFocusRepository.deleteById(id)
      .then(timerFocusRepository.findById(id).hasElement().map { !it })
  }

}
