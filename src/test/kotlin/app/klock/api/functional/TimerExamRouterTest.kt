package app.klock.api.functional

import app.klock.api.config.TestConfig
import app.klock.api.functional.timer.TimerExamDto
import app.klock.api.service.TimerExamService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime
import org.mockito.Mockito.`when` as mockitoWhen

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [TestConfig::class])
@ActiveProfiles("test")
class TimerExamRouterTest @Autowired constructor(
  private val client: WebTestClient
) {
  @MockBean
  private lateinit var timerExamService: TimerExamService

  @Test
  fun `Create Exam Timer`() {
    val testTimerExamDto = TimerExamDto(
      userId = 2L,
      seq = 1,
      name = "Test Exam Timer",
      startTime = LocalDateTime.now(),
      duration = 120,
      questionCount = 50
    )

    val timerExam = testTimerExamDto.toDomain()
    runBlocking { Mockito.`when`(timerExamService.create(timerExam)).thenReturn(timerExam.copy(id = 1L)) }
    val timerExamDto = TimerExamDto.from(timerExam)

    client.post()
      .uri("/api/exam-timers")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(timerExamDto)
      .exchange()
      .expectStatus().isCreated
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.userId").isEqualTo(timerExamDto.userId.toInt())
      .jsonPath("$.seq").isEqualTo(timerExamDto.seq)
      .jsonPath("$.name").isEqualTo(timerExamDto.name)
      .jsonPath("$.duration").isEqualTo(timerExamDto.duration)
      .jsonPath("$.questionCount").isEqualTo(timerExamDto.questionCount)
  }

  @Test
  fun `Update Exam Timer`() {
    val testTimerExamDto = TimerExamDto(
      id = 1L,
      userId = 1L,
      seq = 1,
      name = "Updated Exam Timer",
      startTime = LocalDateTime.now(),
      duration = 120,
      questionCount = 50
    )
    val timerExam = testTimerExamDto.toDomain()
    runBlocking {
      mockitoWhen(timerExamService.get(timerExam.id!!)).thenReturn(timerExam)
      mockitoWhen(timerExamService.update(timerExam)).thenReturn(timerExam)
    }
    val timerExamDto = TimerExamDto.from(timerExam)

    client.post()
      .uri("/api/exam-timers/${timerExamDto.id}")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(timerExamDto)
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.id").isEqualTo(timerExamDto.id!!.toInt())
      .jsonPath("$.userId").isEqualTo(timerExamDto.userId.toInt())
      .jsonPath("$.seq").isEqualTo(timerExamDto.seq)
      .jsonPath("$.name").isEqualTo(timerExamDto.name)
      .jsonPath("$.duration").isEqualTo(timerExamDto.duration)
      .jsonPath("$.questionCount").isEqualTo(timerExamDto.questionCount)
  }

  @Test
  fun `Delete Exam Timer`() {
    val testTimerExamId = 1L

    runBlocking { mockitoWhen(timerExamService.delete(testTimerExamId)).thenReturn(true) }

    client.delete()
      .uri("/api/exam-timers/$testTimerExamId")
      .exchange()
      .expectStatus().isNoContent
  }

}
