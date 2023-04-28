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
  suspend fun createTimerFocus(timerFocus: TimerFocus): TimerFocus = timerFocusRepository.save(timerFocus).awaitSingle()

  // Read TimerFocus by id
  suspend fun getTimerFocusById(id: Long): TimerFocus? = timerFocusRepository.findById(id).awaitFirstOrNull()

  // Update TimerFocus
  suspend fun updateTimerFocus(timerFocus: TimerFocus): TimerFocus = timerFocusRepository.save(timerFocus).awaitSingle()

  // Delete TimerFocus by id
  suspend fun deleteTimerStudyById(id: Long): Boolean {
    val deletedRows = timerFocusRepository.deleteById(id).awaitFirstOrNull()
    return deletedRows != null
  }
}
