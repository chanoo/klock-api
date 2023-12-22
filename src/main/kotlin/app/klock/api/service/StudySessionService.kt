// StudySessionService.kt
package app.klock.api.service

import app.klock.api.domain.entity.StudySession
import app.klock.api.functional.userTrace.CreateStudyTrace
import app.klock.api.functional.userTrace.UserTraceDto
import app.klock.api.repository.StudySessionRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.LocalTime

@Service
class StudySessionService(private val studySessionRepository: StudySessionRepository,
  private val userTraceService: UserTraceService) {

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
    return createStudyTrace(studySession, true)
      .then(Mono.just(studySession))
      .flatMap {
        studySessionRepository.save(it)
      }
  }

  // studySession 수정
  fun update(id: Long, studySession: StudySession): Mono<StudySession> {
    return createStudyTrace(studySession, false)
      .then(Mono.just(studySession))
      .flatMap {
        studySessionRepository.findById(id)
          .flatMap { existingSession ->
            val updatedSession = existingSession.copy(
              startTime = studySession.startTime,
              endTime = studySession.endTime
            )
            studySessionRepository.save(updatedSession)
          }
      }
  }

  private fun createStudyTrace(studySession: StudySession, start: Boolean): Mono<UserTraceDto> {
    val message = if (start) {
      "${studySession.timerName} 공부를 시작했어요."
    } else {
      "${studySession.timerName} 공부를 종료했어요."
    }
    val createStudyTrace = CreateStudyTrace(message)
    return userTraceService.createStudy(studySession.userId, createStudyTrace)
  }
}
