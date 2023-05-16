package app.klock.api.service

import app.klock.api.domain.entity.TimerExam
import app.klock.api.domain.entity.TimerFocus
import app.klock.api.domain.entity.TimerPomodoro
import app.klock.api.functional.timer.TimerSeqDto
import app.klock.api.functional.timer.TimerType
import app.klock.api.repository.TimerExamRepository
import app.klock.api.repository.TimerFocusRepository
import app.klock.api.repository.TimerPomodoroRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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

  private lateinit var slotFocus: CapturingSlot<TimerFocus>
  private lateinit var slotExam: CapturingSlot<TimerExam>
  private lateinit var slotPomodoro: CapturingSlot<TimerPomodoro>

  @BeforeEach
  fun setUp() {
    timerFocusRepository = mockk()
    timerExamRepository = mockk()
    timerPomodoroRepository = mockk()

    timerService = spyk(
      objToCopy = TimerService(timerExamRepository, timerPomodoroRepository, timerFocusRepository),
      recordPrivateCalls = true
    )
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
      seq = 2
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

    verify(exactly = 1) { timerService.updateTimersSeq(timerSeqArray) }
  }

  @Test
  fun `공부시간 타이머 순서 업데이트 테스트`() {
    // Given
    val timerFocusSeq = TimerSeqDto(
      type = TimerType.FOCUS,
      id = 1L,
      seq = 2
    )

    val timerFocus = TimerFocus(1L, 1L, 1, "Focus 1")

    slotFocus = slot()
    every { timerFocusRepository.findById(1L) } returns Mono.just(timerFocus)
    every { timerFocusRepository.save(capture(slotFocus)) } answers { Mono.just(firstArg()) }

    // When
    val updatedFocus: Mono<TimerFocus> = timerService.updateFocus(timerFocusSeq)

    // Then
    StepVerifier.create(updatedFocus)
      .expectNextMatches { updatedTimer ->
        timerFocusSeq.seq == updatedTimer.seq
      }
      .verifyComplete()

    verify(exactly = 1) { timerService.updateFocus(timerFocusSeq) }
    verify(exactly = 1) { timerFocusRepository.save(any()) }

    assertTrue(slotFocus.isCaptured)
    assertEquals(timerFocusSeq.seq, slotFocus.captured.seq)
  }

  @Test
  fun `시험시간 타이머 순서 업데이트 테스트`() {
    // Given
    val timerExamSeq = TimerSeqDto(
      type = TimerType.EXAM,
      id = 1L,
      seq = 2
    )

    val timerExam = TimerExam(1L, 1L, "Exam 1", 1, LocalDateTime.now(), 60, 30)

    slotExam = slot()
    every { timerExamRepository.findById(1L) } returns Mono.just(timerExam)
    every { timerExamRepository.save(capture(slotExam)) } answers { Mono.just(firstArg()) }

    // When
    val updatedExam: Mono<TimerExam> = timerService.updateExam(timerExamSeq)

    // Then
    StepVerifier.create(updatedExam)
      .expectNextMatches { updatedTimer ->
        timerExamSeq.seq == updatedTimer.seq
      }
      .verifyComplete()

    verify(exactly = 1) { timerService.updateExam(timerExamSeq) }
    verify(exactly = 1) { timerExamRepository.save(any()) }

    assertTrue(slotExam.isCaptured)
    assertEquals(timerExamSeq.seq, slotExam.captured.seq)
  }

  @Test
  fun `뽀모도로 타이머 순서 업데이트 테스트`() {
    // Given
    val timerPomodoroSeq = TimerSeqDto(
      type = TimerType.POMODORO,
      id = 1L,
      seq = 3
    )

    val timerPomodoro = TimerPomodoro(1L, 1L, "Pomodoro 1", 1, 25, 5, 4)

    slotPomodoro = slot()
    every { timerPomodoroRepository.findById(1L) } returns Mono.just(timerPomodoro)
    every { timerPomodoroRepository.save(capture(slotPomodoro)) } answers { Mono.just(firstArg()) }

    // When
    val updatedPomodoro: Mono<TimerPomodoro> = timerService.updatePomodoro(timerPomodoroSeq)

    // Then
    StepVerifier.create(updatedPomodoro)
      .expectNextMatches { updatedTimer ->
        timerPomodoroSeq.seq == updatedTimer.seq
      }
      .verifyComplete()

    verify(exactly = 1) { timerService.updatePomodoro(timerPomodoroSeq) }
    verify(exactly = 1) { timerPomodoroRepository.save(any()) }

    assertTrue(slotPomodoro.isCaptured)
    assertEquals(timerPomodoroSeq.seq, slotPomodoro.captured.seq)
  }
}
