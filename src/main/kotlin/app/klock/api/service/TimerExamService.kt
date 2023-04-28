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
  suspend fun createTimerExam(timerExam: TimerExam): TimerExam {
    return timerExamRepository.save(timerExam).awaitSingle()
  }

  // Read TimerExam by id
  suspend fun getTimerExamById(id: Long): TimerExam? {
    return timerExamRepository.findById(id).awaitFirstOrNull()
  }

  // Update TimerExam
  suspend fun updateTimerExam(timerExam: TimerExam): TimerExam {
    return timerExamRepository.save(timerExam).awaitSingle()
  }

  // Delete TimerExam by id
  suspend fun deleteTimerExamById(id: Long): Boolean {
    val deletedRows = timerExamRepository.deleteById(id).awaitFirstOrNull()
    return deletedRows != null
  }
}
