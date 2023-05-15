package app.klock.api.service

import app.klock.api.domain.entity.TimerExam
import app.klock.api.domain.entity.TimerFocus
import app.klock.api.domain.entity.TimerPomodoro
import app.klock.api.functional.timer.TimerDto
import app.klock.api.functional.timer.TimerSeqDto
import app.klock.api.functional.timer.TimerType
import app.klock.api.repository.TimerExamRepository
import app.klock.api.repository.TimerFocusRepository
import app.klock.api.repository.TimerPomodoroRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDateTime

//@ExtendWith(MockitoExtension::class)
class TimerServiceTest {

  private lateinit var timerService: TimerService

  private lateinit var timerExamRepository: TimerExamRepository
  private lateinit var timerPomodoroRepository: TimerPomodoroRepository
  private lateinit var timerFocusRepository: TimerFocusRepository

  @BeforeEach
  fun setUp() {
//    timerExamRepository = Mockito.mock(TimerExamRepository::class.java)
//    timerPomodoroRepository = Mockito.mock(TimerPomodoroRepository::class.java)
//    timerFocusRepository = Mockito.mock(TimerFocusRepository::class.java)
    timerFocusRepository = mockk()
    timerExamRepository = mockk()
    timerPomodoroRepository = mockk()

    timerService = TimerService(timerExamRepository, timerPomodoroRepository, timerFocusRepository)
  }

  @Test
  fun `사용자 ID별 모든 타이머 가져오기 테스트`() {
    // Given
    val userId: Long = 1
    val exam = TimerExam(100, userId, "Exam 1", 1, LocalDateTime.now(), 60, 30)
    val pomodoro = TimerPomodoro(200, userId, "Pomodoro 1", 2, 25, 5, 4)
    val focus = TimerFocus(300, userId, 3, "Focus 1")

    every { timerFocusRepository.findAllByUserIdOrderBySeq(userId) } returns Flux.just(focus)
    every { timerExamRepository.findAllByUserIdOrderBySeq(userId) } returns Flux.just(exam)
    every { timerPomodoroRepository.findAllByUserIdOrderBySeq(userId) } returns Flux.just(pomodoro)

    // When
    val timersFlux = timerService.getAllTimersByUserId(userId)

    // Then
    StepVerifier.create(timersFlux)
      .assertNext { timer ->
        assertEquals(100, timer.id)
      }
      .assertNext { timer ->
        assertEquals(200, timer.id)
      }
      .assertNext { timer ->
        assertEquals(300, timer.id)
      }
      .verifyComplete()
  }

  @Test
  fun `타이머 순서 업데이트 테스트`() {
    // Given
    val timerFocusSeq = TimerSeqDto(
      type = TimerType.FOCUS,
      id = 1L,
      seq = 1
    )
    val timerExamSeq = TimerSeqDto(
      type = TimerType.EXAM,
      id = 1L,
      seq = 2
    )
    val timerPomodoroSeq = TimerSeqDto(
      type = TimerType.POMODORO,
      id = 1L,
      seq = 3
    )
    val timerSeqArray = arrayOf(timerFocusSeq, timerExamSeq, timerPomodoroSeq)

    val timerFocus = TimerFocus(1L, 1L, 1, "Focus 1")
    val timerExam = TimerExam(1L, 1L, "Exam 1", 1, LocalDateTime.now(), 60, 30)
    val timerPomodoro = TimerPomodoro(1L, 1L, "Pomodoro 1", 1, 25, 5, 4)

    every { timerFocusRepository.findById(1L) } returns Mono.just(timerFocus)
    every { timerExamRepository.findById(1L) } returns Mono.just(timerExam)
    every { timerPomodoroRepository.findById(1L) } returns Mono.just(timerPomodoro)

    // When
    val resultMono: Mono<Boolean> = timerService.updateTimersSeq(timerSeqArray)

    // Then
    StepVerifier.create(resultMono)
      .expectNext(true)
      .verifyComplete()
  }
}
