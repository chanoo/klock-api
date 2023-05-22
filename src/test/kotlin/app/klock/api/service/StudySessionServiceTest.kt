// StudySessionServiceTest.kt
package app.klock.api.service

import app.klock.api.domain.entity.StudySession
import app.klock.api.functional.timer.TimerType
import app.klock.api.repository.StudySessionRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@ExtendWith(MockitoExtension::class)
@SpringBootTest(classes = [StudySessionService::class])
class StudySessionServiceTest @Autowired constructor(
  private val studySessionService: StudySessionService
) {
  @MockBean
  private lateinit var studySessionRepository: StudySessionRepository

  private lateinit var studySession: StudySession
  private lateinit var studySessions: List<StudySession>

  @BeforeEach
  fun setUp() {
    // 테스트에 사용할 StudySession 데이터를 설정합니다.
    studySession = StudySession(
      id = 1L,
      userId = 1L,
      startTime = LocalDateTime.now(),
      endTime = LocalDateTime.now().plusHours(2),
      timerName = "국어 공부",
      timerType = TimerType.FOCUS
    )

    studySessions = listOf(
      StudySession(id = 1, userId = 1, startTime = LocalDateTime.now(), endTime = LocalDateTime.now().plusHours(1), "국어", TimerType.FOCUS),
      StudySession(id = 2, userId = 2, startTime = LocalDateTime.now(), endTime = LocalDateTime.now().plusHours(1), "수학", TimerType.POMODORO)
    )
  }

  @Test
  fun `accountId와 startTime 사이의 StudySessions 찾기`() {
    val accountId = 1L
    val startDate = LocalDate.now()
    val startDateTime = startDate.atStartOfDay()
    val endDateTime = startDate.atTime(LocalTime.MAX)

    // findByAccountIdAndStartTimeBetween에 대한 목(mock)을 설정합니다.
    Mockito.`when`(studySessionRepository.findByUserIdAndStartTimeBetween(accountId, startDateTime, endDateTime))
      .thenReturn(Flux.fromIterable(listOf(studySession)))

    val result = studySessionService.findByUserIdAndStartTimeBetween(accountId, startDate)

    StepVerifier.create(result)
      .expectNext(studySession)
      .verifyComplete()
  }

  // create와 update 메소드 테스트
  @Test
  fun `StudySession 생성`() {
    Mockito.`when`(studySessionRepository.save(studySession)).thenReturn(Mono.just(studySession))

    val result = studySessionService.create(studySession)

    StepVerifier.create(result)
      .expectNext(studySession)
      .verifyComplete()
  }

  @Test
  fun `StudySession 수정`() {
    val updatedStudySession = studySession.copy(endTime = studySession.endTime.plusHours(1))

    Mockito.`when`(studySessionRepository.findById(studySession.id!!)).thenReturn(Mono.just(studySession))
    Mockito.`when`(studySessionRepository.save(updatedStudySession)).thenReturn(Mono.just(updatedStudySession))

    val result = studySessionService.update(studySession.id!!, updatedStudySession)

    StepVerifier.create(result)
      .expectNext(updatedStudySession)
      .verifyComplete()
  }
}
