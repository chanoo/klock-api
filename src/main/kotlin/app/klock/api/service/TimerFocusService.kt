package app.klock.api.service

import app.klock.api.domain.entity.TimerFocus
import app.klock.api.repository.TimerFocusRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service

@Service
class TimerFocusService(
  private val timerFocusRepository: TimerFocusRepository
) {
  // Create TimerFocus
  suspend fun create(timerFocus: TimerFocus): TimerFocus = timerFocusRepository.save(timerFocus).awaitSingle()

  // Read TimerFocus by id
  suspend fun get(id: Long): TimerFocus? = timerFocusRepository.findById(id).awaitFirstOrNull()

  // Update TimerFocus
  suspend fun update(timerFocus: TimerFocus): TimerFocus = timerFocusRepository.save(timerFocus).awaitSingle()

  // Delete TimerFocus by id
  suspend fun delete(id: Long): Boolean {
    timerFocusRepository.deleteById(id).awaitFirstOrNull()
    return get(id) == null
  }
}
