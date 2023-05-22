package app.klock.api.functional.studySession

import app.klock.api.domain.entity.StudySession
import app.klock.api.functional.timer.TimerType
import java.time.LocalDateTime

data class StudySessionDto(
  val id: Long? = null,
  val userId: Long,
  val startTime: LocalDateTime,
  val endTime: LocalDateTime,
  val timerName: String,
  val timerType: TimerType
) {
  fun toDomain() = StudySession(id, userId, startTime, endTime, timerName, timerType)

  companion object {
    fun from(domain: StudySession) = StudySessionDto(
      domain.id,
      domain.userId,
      domain.startTime,
      domain.endTime,
      domain.timerName,
      domain.timerType
    )
  }
}
