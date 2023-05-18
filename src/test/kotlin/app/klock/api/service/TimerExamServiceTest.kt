package app.klock.api.service

import app.klock.api.domain.entity.TimerExam
import app.klock.api.repository.TimerExamRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDateTime

class TimerExamServiceTest {

  private lateinit var timerExamService: TimerExamService
  private lateinit var timerExamRepository: TimerExamRepository

  @BeforeEach
  fun setUp() {
    timerExamRepository = mockk<TimerExamRepository>()
    timerExamService = TimerExamService(timerExamRepository)
  }

  @Test
  fun `TimerExam 생성 테스트`() {
    // Given
    val timerExam = TimerExam(1L, 1, "Exam 1", 1, LocalDateTime.now(), 60, 30, 10)

    every { timerExamRepository.save(timerExam) } returns Mono.just(timerExam)

    // When
    val createdTimerExamMono = timerExamService.create(timerExam)

    // Then
    StepVerifier.create(createdTimerExamMono)
      .assertNext { createdTimerExam ->
        assertEquals(timerExam, createdTimerExam)
      }
      .verifyComplete()
  }

  @Test
  fun `TimerExam 조회 테스트`() {
    // Given
    val timerExamId = 1L
    val timerExam = TimerExam(timerExamId, 1, "Exam 1", 1, LocalDateTime.now(), 60, 30, 10)

    every { timerExamRepository.findById(timerExamId) } returns Mono.just(timerExam)

    // When
    val foundTimerExamMono = timerExamService.get(timerExamId)

    // Then
    StepVerifier.create(foundTimerExamMono)
      .assertNext { foundTimerExam ->
        assertEquals(timerExam, foundTimerExam)
      }
      .verifyComplete()
  }

  @Test
  fun `TimerExam 업데이트 테스트`() {
    // Given
    val id = 1L
    val timerExam = TimerExam(id, 1, "Exam 1", 1, LocalDateTime.now(), 60, 30, 10)
    val updatedTimerExam = TimerExam(id, 1, "Exam 1 Updated", 1, LocalDateTime.now(), 60, 30, 20)

    every { timerExamRepository.findById(id) } returns Mono.just(timerExam)
    every { timerExamRepository.save(timerExam) } returns Mono.just(updatedTimerExam)

    // When
    val updatedTimerExamMono = timerExamService.update(id, timerExam)

    // Then
    StepVerifier.create(updatedTimerExamMono)
      .assertNext { updatedTimerExam ->
        assertEquals("Exam 1 Updated", updatedTimerExam.name)
      }
      .verifyComplete()
  }

  @Test
  fun `TimerExam 삭제 테스트`() {
    // Given
    val timerExamId = 1L

    every { timerExamRepository.deleteById(timerExamId) } returns Mono.empty<Void>()
    every { timerExamRepository.findById(timerExamId) } returns Mono.empty()

    // When
    val deleteResultMono = timerExamService.delete(timerExamId)

    // Then
    StepVerifier.create(deleteResultMono)
      .assertNext { deleteResult ->
        assertTrue(deleteResult)
      }
      .verifyComplete()
  }

}
