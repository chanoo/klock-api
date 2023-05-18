package app.klock.api.service

import app.klock.api.domain.entity.TimerPomodoro
import app.klock.api.repository.TimerPomodoroRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class TimerPomodoroServiceTest {

  private lateinit var timerPomodoroService: TimerPomodoroService
  private lateinit var timerPomodoroRepository: TimerPomodoroRepository

  @BeforeEach
  fun setUp() {
    timerPomodoroRepository = mockk()
    timerPomodoroService = TimerPomodoroService(timerPomodoroRepository)
  }

  @Test
  fun `타이머 생성 테스트`() {
    // Given
    val timerPomodoro = TimerPomodoro(
      userId = 1L,
      name = "Pomodoro 1",
      seq = 1,
      focusTime = 25,
      breakTime = 5,
      cycleCount = 4
    )

    every { timerPomodoroRepository.save(timerPomodoro) } returns Mono.just(timerPomodoro)

    // When
    val createdTimerPomodoroMono = timerPomodoroService.create(timerPomodoro)

    // Then
    StepVerifier.create(createdTimerPomodoroMono)
      .assertNext { createdTimerPomodoro ->
        assertEquals(timerPomodoro, createdTimerPomodoro)
      }
      .verifyComplete()
  }

  @Test
  fun `ID별 타이머 가져오기 테스트`() {
    // Given
    val timerPomodoro = TimerPomodoro(
      id = 1L,
      userId = 1L,
      name = "Pomodoro 1",
      seq = 1,
      focusTime = 25,
      breakTime = 5,
      cycleCount = 4
    )

    every { timerPomodoroRepository.findById(1L) } returns Mono.justOrEmpty(timerPomodoro)

    // When
    val foundTimerPomodoroMono = timerPomodoroService.get(1L)

    // Then
    StepVerifier.create(foundTimerPomodoroMono)
      .assertNext { foundTimerPomodoro ->
        assertEquals(timerPomodoro, foundTimerPomodoro)
      }
      .verifyComplete()
  }

  @Test
  fun `타이머 업데이트 테스트`() {
    // Given
    val timerPomodoro = TimerPomodoro(
      id = 1L,
      userId = 1L,
      name = "Pomodoro 1",
      seq = 1,
      focusTime = 25,
      breakTime = 5,
      cycleCount = 4
    )

    every { timerPomodoroRepository.save(timerPomodoro) } returns Mono.just(timerPomodoro)

    // When
    val updatedTimerPomodoroMono = timerPomodoroService.update(timerPomodoro)

    // Then
    StepVerifier.create(updatedTimerPomodoroMono)
      .assertNext { updatedTimerPomodoro ->
        assertEquals(timerPomodoro, updatedTimerPomodoro)
      }
      .verifyComplete()
  }

  @Test
  fun `타이머 삭제 테스트`() {
    // Given
    val timerPomodoroId = 1L

    every { timerPomodoroRepository.deleteById(timerPomodoroId) } returns Mono.empty<Void>()
    every { timerPomodoroRepository.findById(timerPomodoroId) } returns Mono.empty()

    // When
    val deleteResultMono = timerPomodoroService.delete(timerPomodoroId)

    // Then
    StepVerifier.create(deleteResultMono)
      .assertNext { deleteResult ->
        assertTrue(deleteResult)
      }
      .verifyComplete()
  }
}
