package app.klock.api.service

import app.klock.api.domain.entity.StudySession
import app.klock.api.repository.StudySessionRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.time.LocalDate
import java.time.LocalTime

@Service
class StudySessionService(private val studySessionRepository: StudySessionRepository) {

    // userid와 startTime으로 studySession 찾기
    fun findByAccountIdAndStartTimeBetween(userId: Long, startDate: LocalDate): Flux<StudySession> {
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = startDate.atTime(LocalTime.MAX)
        return studySessionRepository.findByAccountIdAndStartTimeBetween(userId, startDateTime, endDateTime)
    }}
