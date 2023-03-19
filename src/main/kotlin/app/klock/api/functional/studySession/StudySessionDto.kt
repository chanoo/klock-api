package app.klock.api.functional.studySession.dto

import java.time.LocalDateTime

data class StudySessionDto(
    val id: Long? = null,
    val accountId: Long,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
)
