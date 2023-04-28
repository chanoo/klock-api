package app.klock.api.service

import app.klock.api.domain.entity.TimerExam
import app.klock.api.domain.entity.TimerFocus
import app.klock.api.domain.entity.TimerPomodoro
import app.klock.api.repository.TimerExamRepository
import app.klock.api.repository.TimerFocusRepository
import app.klock.api.repository.TimerPomodoroRepository
import kotlinx.coroutines.reactor.mono
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class TimerServiceTest {

  private lateinit var timerService: TimerService

  private lateinit var timerExamRepository: TimerExamRepository
  private lateinit var timerPomodoroRepository: TimerPomodoroRepository
  private lateinit var timerFocusRepository: TimerFocusRepository

  @BeforeEach
  fun setUp() {
    timerExamRepository = Mockito.mock(TimerExamRepository::class.java)
    timerPomodoroRepository = Mockito.mock(TimerPomodoroRepository::class.java)
    timerFocusRepository = Mockito.mock(TimerFocusRepository::class.java)

    timerService = TimerService(timerExamRepository, timerPomodoroRepository, timerFocusRepository)
  }

  @Test
  fun `사용자 ID별 모든 타이머 가져오기 테스트`() {
    // Given
    val userId: Long = 1
    val exam = TimerExam(100, userId, "Exam 1", 1, LocalDateTime.now(), 60, 30)
    val pomodoro = TimerPomodoro(200, userId, "Pomodoro 1", 2, 25, 5, 4)
    val focus = TimerFocus(300, userId, 3, "Focus 1")

    `when`(timerExamRepository.findAllByUserIdOrderBySeq(userId)).thenReturn(Flux.just(exam))
    `when`(timerPomodoroRepository.findAllByUserIdOrderBySeq(userId)).thenReturn(Flux.just(pomodoro))
    `when`(timerFocusRepository.findAllByUserIdOrderBySeq(userId)).thenReturn(Flux.just(focus))

    // When
    val timersMono = mono { timerService.getAllTimersByUserId(userId) }

    // Then
    StepVerifier.create(timersMono)
      .assertNext { timers ->
        assertEquals(3, timers.size)
        assertEquals(100, timers[0].id)
        assertEquals(200, timers[1].id)
        assertEquals(300, timers[2].id)
      }
      .verifyComplete()
  }
}
