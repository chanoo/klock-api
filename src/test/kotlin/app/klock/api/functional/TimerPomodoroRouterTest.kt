package app.klock.api.functional

import app.klock.api.functional.timer.TimerPomodoroDto
import app.klock.api.functional.timer.TimerPomodoroHandler
import app.klock.api.functional.timer.TimerPomodoroRouter
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
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

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TimerPomodoroRouterTest {

  private lateinit var timerPomodoroRouter: TimerPomodoroRouter
  private val timerPomodoroHandler = mockk<TimerPomodoroHandler>()

  @BeforeEach
  fun setUp() {
    timerPomodoroRouter = TimerPomodoroRouter(timerPomodoroHandler)
  }

  @Test
  fun `POST 요청으로 Pomodoro 타이머 생성 테스트`() {
    val timerPomodoroDto = TimerPomodoroDto(
      userId = 2L,
      seq = 1,
      name = "Pomodoro Timer",
      focusTime = 25,
      restTime = 5,
      cycleCount = 4
    )

    val createdTimerPomodoroDto = timerPomodoroDto.copy(id = 1L)

    coEvery { timerPomodoroHandler.createPomodoroTimer(any()) } coAnswers {
      ServerResponse.status(HttpStatus.CREATED).bodyValueAndAwait(createdTimerPomodoroDto)
    }

    val client = WebTestClient.bindToRouterFunction(timerPomodoroRouter.timerPomodoroRoutes()).build()

    client.post()
      .uri("/api/pomodoro-timers")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(timerPomodoroDto)
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.CREATED)
      .expectBody(TimerPomodoroDto::class.java)
      .value { actualTimerPomodoroDto ->
        assertEquals(1L, actualTimerPomodoroDto.id, "ID가 1이어야 합니다.")
        assertEquals(timerPomodoroDto.userId, actualTimerPomodoroDto.userId)
        assertEquals(timerPomodoroDto.seq, actualTimerPomodoroDto.seq)
        assertEquals(timerPomodoroDto.type, actualTimerPomodoroDto.type)
        assertEquals(timerPomodoroDto.name, actualTimerPomodoroDto.name)
        assertEquals(timerPomodoroDto.focusTime, actualTimerPomodoroDto.focusTime)
        assertEquals(timerPomodoroDto.restTime, actualTimerPomodoroDto.restTime)
        assertEquals(timerPomodoroDto.cycleCount, actualTimerPomodoroDto.cycleCount)
      }
  }

  @Test
  fun `POST 요청으로 Pomodoro 타이머 수정 테스트`() {
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

    coEvery { timerPomodoroHandler.updatePomodoroTimer(any()) } coAnswers {
      ServerResponse.ok().bodyValueAndAwait(updatedTimerPomodoroDto)
    }

    val client = WebTestClient.bindToRouterFunction(timerPomodoroRouter.timerPomodoroRoutes()).build()

    client.post()
      .uri("/api/pomodoro-timers/$timerId")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(updatedTimerPomodoroDto)
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.OK)
      .expectBody(TimerPomodoroDto::class.java)
      .isEqualTo(updatedTimerPomodoroDto)
  }

  @Test
  fun `DELETE 요청으로 Pomodoro 타이머 삭제 테스트`() {
    val timerId = 30L

    coEvery { timerPomodoroHandler.deletePomodoroTimer(any()) } coAnswers {
      ServerResponse.noContent().buildAndAwait()
    }

    val client = WebTestClient.bindToRouterFunction(timerPomodoroRouter.timerPomodoroRoutes()).build()

    client.delete()
      .uri("/api/pomodoro-timers/$timerId")
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.NO_CONTENT)
  }
}
