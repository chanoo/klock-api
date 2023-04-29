package app.klock.api.service

import app.klock.api.domain.entity.TimerPomodoro
import app.klock.api.repository.TimerPomodoroRepository
import kotlinx.coroutines.reactor.mono
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(MockitoExtension::class)
class TimerPomodoroServiceTest {

  private lateinit var timerPomodoroService: TimerPomodoroService
  private lateinit var timerPomodoroRepository: TimerPomodoroRepository

  @BeforeEach
  fun setUp() {
    timerPomodoroRepository = Mockito.mock(TimerPomodoroRepository::class.java)
    timerPomodoroService = TimerPomodoroService(timerPomodoroRepository)
  }

  @Test
  fun `타이머 생성 테스트`() {
    // Given
    val timerPomodoro = TimerPomodoro(
      id = 1L,
      userId = 1L,
      name = "Pomodoro 1",
      seq = 1,
      focusTime = 25,
      restTime = 5,
      cycleCount = 4
    )

    `when`(timerPomodoroRepository.save(timerPomodoro)).thenReturn(Mono.just(timerPomodoro))

    // When
    val createdTimerPomodoroMono = mono { timerPomodoroService.create(timerPomodoro) }

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
      restTime = 5,
      cycleCount = 4
    )

    `when`(timerPomodoroRepository.findById(1L)).thenReturn(Mono.justOrEmpty(timerPomodoro))

    // When
    val foundTimerPomodoroMono = mono { timerPomodoroService.get(1L) }

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
      restTime = 5,
      cycleCount = 4
    )

    `when`(timerPomodoroRepository.save(timerPomodoro)).thenReturn(Mono.just(timerPomodoro))

    // When
    val updatedTimerPomodoroMono = mono { timerPomodoroService.update(timerPomodoro) }

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

    `when`(timerPomodoroRepository.deleteById(timerPomodoroId)).thenReturn(Mono.empty<Void>())
    `when`(timerPomodoroRepository.findById(timerPomodoroId)).thenReturn(Mono.empty())

    // When
    val deleteResultMono = mono { timerPomodoroService.delete(timerPomodoroId) }

    // Then
    StepVerifier.create(deleteResultMono)
      .assertNext { deleteResult ->
        assertTrue(deleteResult)
      }
      .verifyComplete()
  }
}
