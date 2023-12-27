// StudySessionService.kt
package app.klock.api.service

import app.klock.api.domain.entity.StudySession
import app.klock.api.repository.StudySessionRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.LocalTime

@Service
class StudySessionService(private val studySessionRepository: StudySessionRepository) {

  // userId로 startTime으로 studySession 찾기
  fun findByUserIdAndStartTimeBetween(userId: Long, startDate: LocalDate): Flux<StudySession> {
    val startDateTime = startDate.atStartOfDay()
    val endDateTime = startDate.atTime(LocalTime.MAX)
    return studySessionRepository.findByUserIdAndStartTimeBetween(userId, startDateTime, endDateTime)
  }

  fun findByUserIdAndStartTimeBetween(userId: Long, startDate: LocalDate, endDate: LocalDate): Flux<StudySession> {
    val startDateTime = startDate.atStartOfDay()
    val endDateTime = endDate.atTime(LocalTime.MAX)
    return studySessionRepository.findByUserIdAndStartTimeBetween(userId, startDateTime, endDateTime)
  }

  // userId로 studySession 등록
  fun create(studySession: StudySession): Mono<StudySession> {
    return studySessionRepository.save(studySession)
  }

  // studySession 수정
  fun update(id: Long, studySession: StudySession): Mono<StudySession> {
    return studySessionRepository.findById(id)
      .flatMap { existingSession ->
        studySessionRepository.save(
          existingSession.updateWith(studySession)
        )
      }
  }

  private fun StudySession.updateWith(newData: StudySession): StudySession {
    return this.copy(
      startTime = newData.startTime?.takeIf { it != startTime } ?: startTime,
      endTime = newData.endTime?.takeIf { it != endTime } ?: endTime,
      timerName = newData.timerName?.takeIf { it != timerName } ?: timerName
    )
  }
}
