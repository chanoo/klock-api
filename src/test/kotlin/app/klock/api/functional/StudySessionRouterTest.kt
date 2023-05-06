package app.klock.api.functional

import app.klock.api.functional.studySession.StudySessionHandler
import app.klock.api.functional.studySession.StudySessionRouter
import app.klock.api.functional.studySession.dto.StudySessionDTO
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
      StudySessionDTO(
        id = 1,
        userId = userId,
        startTime = LocalDateTime.now(),
        endTime = LocalDateTime.now().plusHours(1)
      ),
      StudySessionDTO(
        id = 2,
        userId = userId,
        startTime = LocalDateTime.now(),
        endTime = LocalDateTime.now().plusHours(1)
      )
    )

    coEvery { studySessionHandler.getStudySessionByUserIdAndDate(any()) } coAnswers {
      ServerResponse.ok().bodyValue(studySessions)
    }

    client.get()
      .uri("/api/study-sessions?userId=$userId&date=$startDate")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBodyList(StudySessionDTO::class.java)
      .hasSize(2)
      .contains(studySessions[0], studySessions[1])
  }

  @Test
  fun `공부 시간 추가`() {
    val studySessionDTO = StudySessionDTO(
      userId = 3,
      startTime = LocalDateTime.now(),
      endTime = LocalDateTime.now().plusHours(1)
    )

    val createdStudySession = studySessionDTO.copy(id = 3)

    coEvery { studySessionHandler.create(any()) } coAnswers {
      ServerResponse.status(201).bodyValue(createdStudySession)
    }

    client.post()
      .uri("/api/study-sessions")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(studySessionDTO)
      .exchange()
      .expectStatus().isCreated
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody(StudySessionDTO::class.java)
      .isEqualTo(createdStudySession)
  }

  @Test
  fun `공부 시간 수정`() {
    val studySessionDTO = StudySessionDTO(
      id = 1L,
      userId = 3,
      startTime = LocalDateTime.now(),
      endTime = LocalDateTime.now().plusHours(1)
    )

    coEvery { studySessionHandler.update(any()) } coAnswers {
      ServerResponse.ok().bodyValue(studySessionDTO)
    }

    client.put()
      .uri("/api/study-sessions/${studySessionDTO.id}")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(studySessionDTO)
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody(StudySessionDTO::class.java)
      .isEqualTo(studySessionDTO)
  }
}
