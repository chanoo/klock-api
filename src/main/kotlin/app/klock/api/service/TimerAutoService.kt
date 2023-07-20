package app.klock.api.service

import app.klock.api.domain.entity.TimerAuto
import app.klock.api.repository.TimerAutoRepository
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TimerAutoService(
  private val timerAutoRepository: TimerAutoRepository
) {
  // Create TimerAuto
  fun create(timerAuto: TimerAuto): Mono<TimerAuto> = timerAutoRepository.save(timerAuto)

  // Read TimerAuto by id
  @PreAuthorize("@permissionService.hasTimerAutoPermission(#id, principal)")
  fun get(id: Long): Mono<TimerAuto> = timerAutoRepository.findById(id)

  // Update TimerAuto
  @PreAuthorize("@permissionService.hasTimerAutoPermission(#timerAuto.id, principal)")
  fun update(timerAuto: TimerAuto): Mono<TimerAuto> = timerAutoRepository.save(timerAuto)

  // Delete TimerAuto by id
  @PreAuthorize("@permissionService.hasTimerAutoPermission(#id, principal)")
  fun delete(id: Long): Mono<Boolean> {
    return timerAutoRepository.deleteById(id)
      .then(timerAutoRepository.findById(id).hasElement().map { !it })
  }

}
