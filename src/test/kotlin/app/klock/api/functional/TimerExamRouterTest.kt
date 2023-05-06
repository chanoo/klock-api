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
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
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
      questionCount = 50
    )
    val createdTimerDto = timerExamDto.copy(id = 1L)

    coEvery { timerExamHandler.createExamTimer(any()) } coAnswers {
      ServerResponse.status(HttpStatus.CREATED).bodyValueAndAwait(createdTimerDto)
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
      }
  }

  @Test
  fun `POST 요청으로 Exam 타이머 수정 테스트`() {
    val timerId = 31L
    val updatedTimerPomodoroDto = TimerPomodoroDto(
      id = timerId,
      userId = 2L,
      seq = 1,
      name = "Updated Pomodoro Timer",
      focusTime = 30,
      restTime = 10,
      cycleCount = 4
    )

    coEvery { timerExamHandler.updateExamTimer(any()) } coAnswers {
      ServerResponse.ok().bodyValueAndAwait(updatedTimerPomodoroDto)
    }

    client.post()
      .uri("/api/exam-timers/$timerId")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(updatedTimerPomodoroDto)
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.OK)
      .expectBody(TimerPomodoroDto::class.java)
      .isEqualTo(updatedTimerPomodoroDto)
  }

  @Test
  fun `DELETE 요청으로 Exam 타이머 삭제 테스트`() {
    val timerId = 30L

    coEvery { timerExamHandler.deleteExamTimer(any()) } coAnswers {
      ServerResponse.noContent().buildAndAwait()
    }

    client.delete()
      .uri("/api/exam-timers/$timerId")
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.NO_CONTENT)
  }

}
