package app.klock.api.functional.studySession.dto

import java.time.LocalDate
import java.time.LocalDateTime

// userid, start_time request
data class StudySessionByUserIdAndDateRequest(val userId: Long, val date: LocalDate)

data class CreateStudySessionRequest(val userId: Long)
data class CreateStudySessionResponse(val startTime: LocalDateTime, val endTime: LocalDateTime, val userId: Long)
