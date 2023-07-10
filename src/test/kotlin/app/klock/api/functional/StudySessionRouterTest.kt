package app.klock.api.functional

import app.klock.api.functional.studySession.StudySessionDto
import app.klock.api.functional.studySession.StudySessionHandler
import app.klock.api.functional.studySession.StudySessionRouter
import app.klock.api.functional.timer.TimerType
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.ServerResponse
import java.time.LocalDateTime

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudySessionRouterTest {

  private lateinit var studySessionRouter: StudySessionRouter
  private val studySessionHandler = mockk<StudySessionHandler>()

  private lateinit var client: WebTestClient

  @BeforeEach
  fun setUp() {
    studySessionRouter = StudySessionRouter(studySessionHandler)

    client = WebTestClient.bindToRouterFunction(studySessionRouter.studySessionRoutes()).build()
  }

  @Test
  fun `특정 사용자의 공부 시간 조회`() {
    val userId = 1L
    val startDate = LocalDateTime.now().toLocalDate()

    val studySessions = listOf(
      StudySessionDto(
        id = 1,
        userId = userId,
        startTime = LocalDateTime.now(),
        endTime = LocalDateTime.now().plusHours(1),
        timerName = "국어",
        timerType = TimerType.FOCUS
      ),
      StudySessionDto(
        id = 2,
        userId = userId,
        startTime = LocalDateTime.now(),
        endTime = LocalDateTime.now().plusHours(1),
        timerName = "수학",
        timerType = TimerType.POMODORO
      )
    )

    coEvery { studySessionHandler.getStudySessionByUserIdAndDate(any()) } coAnswers {
      ServerResponse.ok().bodyValue(studySessions)
    }

    client.get()
      .uri("/api/v1/study-sessions?userId=$userId&date=$startDate")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBodyList(StudySessionDto::class.java)
      .hasSize(2)
      .contains(studySessions[0], studySessions[1])
  }

  @Test
  fun `공부 시간 추가`() {
    val studySessionDTO = StudySessionDto(
      userId = 3,
      startTime = LocalDateTime.now(),
      endTime = LocalDateTime.now().plusHours(1),
      timerName = "국어",
      timerType = TimerType.FOCUS
    )

    val createdStudySession = studySessionDTO.copy(id = 3)

    coEvery { studySessionHandler.create(any()) } coAnswers {
      ServerResponse.status(201).bodyValue(createdStudySession)
    }

    client.post()
      .uri("/api/v1/study-sessions")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(studySessionDTO)
      .exchange()
      .expectStatus().isCreated
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody(StudySessionDto::class.java)
      .isEqualTo(createdStudySession)
  }

  @Test
  fun `공부 시간 수정`() {
    val studySessionDTO = StudySessionDto(
      id = 1L,
      userId = 3,
      startTime = LocalDateTime.now(),
      endTime = LocalDateTime.now().plusHours(1),
      timerName = "국어",
      timerType = TimerType.FOCUS
    )

    coEvery { studySessionHandler.update(any()) } coAnswers {
      ServerResponse.ok().bodyValue(studySessionDTO)
    }

    client.put()
      .uri("/api/v1/study-sessions/${studySessionDTO.id}")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(studySessionDTO)
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody(StudySessionDto::class.java)
      .isEqualTo(studySessionDTO)
  }
}
