package app.klock.api.functional

import app.klock.api.functional.timer.*
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.ServerResponse
import java.time.LocalDateTime

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TimerExamRouterTest {

  private lateinit var timerExamRouter: TimerExamRouter
  private val timerExamHandler = mockk<TimerExamHandler>()

  private lateinit var client: WebTestClient

  @BeforeEach
  fun setUp() {
    timerExamRouter = TimerExamRouter(timerExamHandler)

    client = WebTestClient.bindToRouterFunction(timerExamRouter.timerExamRoutes()).build()
  }

  @Test
  fun `POST 요청으로 Exam 타이머 생성 테스트`() {
    val timerExamDto = TimerExamDto(
      userId = 2L,
      seq = 1,
      name = "Test Exam Timer",
      startTime = LocalDateTime.now(),
      duration = 120,
      questionCount = 50,
      markingTime = 10
    )
    val createdTimerDto = timerExamDto.copy(id = 1L)

    coEvery { timerExamHandler.createExamTimer(any()) } coAnswers {
      ServerResponse.status(HttpStatus.CREATED).bodyValue(createdTimerDto)
    }

    client.post()
      .uri("/api/exam-timers")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(timerExamDto)
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.CREATED)
      .expectBody(timerExamDto::class.java)
      .value { actualTimerExamDto ->
        Assertions.assertEquals(1L, actualTimerExamDto.id, "ID가 1이어야 합니다.")
        Assertions.assertEquals(timerExamDto.userId, actualTimerExamDto.userId)
        Assertions.assertEquals(timerExamDto.seq, actualTimerExamDto.seq)
        Assertions.assertEquals(timerExamDto.type, actualTimerExamDto.type)
        Assertions.assertEquals(timerExamDto.name, actualTimerExamDto.name)
        Assertions.assertEquals(timerExamDto.startTime, actualTimerExamDto.startTime)
        Assertions.assertEquals(timerExamDto.duration, actualTimerExamDto.duration)
        Assertions.assertEquals(timerExamDto.questionCount, actualTimerExamDto.questionCount)
        Assertions.assertEquals(timerExamDto.markingTime, actualTimerExamDto.markingTime)
      }
  }

  @Test
  fun `PUT 요청으로 Exam 타이머 수정 테스트`() {
    val timerId = 31L
    val updatedTimerExamDto = TimerExamDto(
      id = timerId,
      userId = 2L,
      seq = 1,
      name = "Updated Exam Timer",
      startTime = LocalDateTime.now(),
      duration = 120,
      questionCount = 50,
      markingTime = 10
    )

    coEvery { timerExamHandler.updateExamTimer(any()) } coAnswers {
      ServerResponse.ok().bodyValue(updatedTimerExamDto)
    }

    client.put()
      .uri("/api/exam-timers/$timerId")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(updatedTimerExamDto)
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.OK)
      .expectBody(TimerExamDto::class.java)
      .isEqualTo(updatedTimerExamDto)
  }

  @Test
  fun `DELETE 요청으로 Exam 타이머 삭제 테스트`() {
    val timerId = 30L

    coEvery { timerExamHandler.deleteExamTimer(any()) } coAnswers {
      ServerResponse.noContent().build()
    }

    client.delete()
      .uri("/api/exam-timers/$timerId")
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.NO_CONTENT)
  }

}
