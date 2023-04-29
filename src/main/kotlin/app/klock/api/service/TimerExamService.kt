package app.klock.api.service

import app.klock.api.domain.entity.TimerExam
import app.klock.api.repository.TimerExamRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service

@Service
class TimerExamService(
  private val timerExamRepository: TimerExamRepository
) {
  // Create TimerExam
  suspend fun create(timerExam: TimerExam): TimerExam = timerExamRepository.save(timerExam).awaitSingle()

  // Read TimerExam by id
  suspend fun get(id: Long): TimerExam? = timerExamRepository.findById(id).awaitFirstOrNull()

  // Update TimerExam
  suspend fun update(timerExam: TimerExam): TimerExam = timerExamRepository.save(timerExam).awaitSingle()

  // Delete TimerExam by id
  suspend fun delete(id: Long): Boolean {
    timerExamRepository.deleteById(id).awaitFirstOrNull()
    return get(id) == null
  }
}
