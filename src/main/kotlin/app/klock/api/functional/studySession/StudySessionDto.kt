package app.klock.api.functional.studySession.dto

import java.time.LocalDateTime

data class StudySessionDTO(
  val id: Long? = null,
  val userId: Long,
  val startTime: LocalDateTime,
  val endTime: LocalDateTime,
)
