package app.klock.api.service

import app.klock.api.domain.entity.TimerPomodoro
import app.klock.api.repository.TimerPomodoroRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service

@Service
class TimerPomodoroService(
  private val timerPomodoroRepository: TimerPomodoroRepository
) {
  // Create TimerPomodoro
  suspend fun create(timerPomodoro: TimerPomodoro): TimerPomodoro = timerPomodoroRepository.save(timerPomodoro).awaitSingle()

  // Read TimerPomodoro by id
  suspend fun get(id: Long): TimerPomodoro? = timerPomodoroRepository.findById(id).awaitFirstOrNull()

  // Update TimerPomodoro
  suspend fun update(timerPomodoro: TimerPomodoro): TimerPomodoro = timerPomodoroRepository.save(timerPomodoro).awaitSingle()

  // Delete TimerPomodoro by id
  suspend fun delete(id: Long): Boolean {
    timerPomodoroRepository.deleteById(id).awaitFirstOrNull()
    return get(id) == null
  }
}
