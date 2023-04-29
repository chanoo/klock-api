package app.klock.api.functional

import app.klock.api.config.TestConfig
import app.klock.api.functional.timer.TimerPomodoroDto
import app.klock.api.service.TimerPomodoroService
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
import org.mockito.Mockito.`when` as mockitoWhen

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [TestConfig::class])
@ActiveProfiles("test")
class TimerPomodoroRouterTest @Autowired constructor(
  private val client: WebTestClient
) {
  @MockBean
  private lateinit var timerPomodoroService: TimerPomodoroService

  @Test
  fun `Create TimerPomodoro`() {
    val srcTimerPomodoroDto = TimerPomodoroDto(
      userId = 2L,
      seq = 1,
      name = "Pomodoro Timer",
      focusTime = 25,
      restTime = 5,
      cycleCount = 4
    )

    val timerPomodoro = srcTimerPomodoroDto.toDomain()
    runBlocking { Mockito.`when`(timerPomodoroService.create(timerPomodoro)).thenReturn(timerPomodoro.copy(id = 3L)) }
    val timerPomodoroDto = TimerPomodoroDto.from(timerPomodoro)

    client.post()
      .uri("/api/pomodoro-timers")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(timerPomodoroDto)
      .exchange()
      .expectStatus().isCreated
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.userId").isEqualTo(timerPomodoroDto.userId.toInt())
      .jsonPath("$.seq").isEqualTo(timerPomodoroDto.seq)
      .jsonPath("$.name").isEqualTo(timerPomodoroDto.name)
      .jsonPath("$.focusTime").isEqualTo(timerPomodoroDto.focusTime)
      .jsonPath("$.restTime").isEqualTo(timerPomodoroDto.restTime)
      .jsonPath("$.cycleCount").isEqualTo(timerPomodoroDto.cycleCount)
  }

  // Update TimerPomodoro
  @Test
  fun `Update TimerPomodoro`() {
    val srcTimerPomodoroDto = TimerPomodoroDto(
      id = 3L,
      userId = 2L,
      seq = 1,
      name = "Pomodoro Timer",
      focusTime = 25,
      restTime = 5,
      cycleCount = 4
    )
    val timerPomodoro = srcTimerPomodoroDto.toDomain()
    runBlocking {
      mockitoWhen(timerPomodoroService.get(timerPomodoro.id!!)).thenReturn(timerPomodoro)
      mockitoWhen(timerPomodoroService.update(timerPomodoro)).thenReturn(timerPomodoro)
    }
    val timerPomodoroDto = TimerPomodoroDto.from(timerPomodoro)

    client.post()
      .uri("/api/pomodoro-timers/${timerPomodoroDto.id}")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(timerPomodoroDto)
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.id").isEqualTo(timerPomodoroDto.id!!.toInt())
      .jsonPath("$.userId").isEqualTo(timerPomodoroDto.userId.toInt())
      .jsonPath("$.seq").isEqualTo(timerPomodoroDto.seq)
      .jsonPath("$.name").isEqualTo(timerPomodoroDto.name)
      .jsonPath("$.focusTime").isEqualTo(timerPomodoroDto.focusTime)
      .jsonPath("$.restTime").isEqualTo(timerPomodoroDto.restTime)
      .jsonPath("$.cycleCount").isEqualTo(timerPomodoroDto.cycleCount)
  }

  // Delete TimerPomodoro
  @Test
  fun `Delete TimerPomodoro`() {
    val testTimerPomodoroId = 1L

    runBlocking { mockitoWhen(timerPomodoroService.delete(testTimerPomodoroId)).thenReturn(true) }

    client.delete()
      .uri("/api/pomodoro-timers/${testTimerPomodoroId}")
      .exchange()
      .expectStatus().isNoContent
  }

}
