package app.klock.api.service

import app.klock.api.domain.entity.TimerExam
import app.klock.api.repository.TimerExamRepository
import app.klock.api.repository.TimerFocusRepository
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
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class TimerExamServiceTest {

  private lateinit var timerExamService: TimerExamService
  private lateinit var timerFocusRepository: TimerFocusRepository
  private lateinit var timerPomodoroRepository: TimerPomodoroRepository
  private lateinit var timerExamRepository: TimerExamRepository
  private lateinit var permissionService: PermissionService

  @BeforeEach
  fun setUp() {
    timerFocusRepository = Mockito.mock(TimerFocusRepository::class.java)
    timerPomodoroRepository = Mockito.mock(TimerPomodoroRepository::class.java)
    timerExamRepository = Mockito.mock(TimerExamRepository::class.java)

    permissionService = PermissionService(
      timerFocusRepository,
      timerPomodoroRepository,
      timerExamRepository
    )

    timerExamService = TimerExamService(timerExamRepository, permissionService)
  }

  @Test
  fun `TimerExam 생성 테스트`() {
    // Given
    val timerExam = TimerExam(1L, 1, "Exam 1", 1, LocalDateTime.now(), 60, 30)

    `when`(timerExamRepository.save(timerExam)).thenReturn(Mono.just(timerExam))

    // When
    val createdTimerExamMono = mono { timerExamService.create(timerExam) }

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
    val timerExam = TimerExam(timerExamId, 1, "Exam 1", 1, LocalDateTime.now(), 60, 30)

    `when`(timerExamRepository.findById(timerExamId)).thenReturn(Mono.just(timerExam))

    // When
    val foundTimerExamMono = mono { timerExamService.get(timerExamId) }

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
    val timerExam = TimerExam(1L, 1, "Exam 1 Updated", 1, LocalDateTime.now(), 60, 30)

    `when`(timerExamRepository.save(timerExam)).thenReturn(Mono.just(timerExam))

    // When
    val updatedTimerExamMono = mono { timerExamService.update(timerExam) }

    // Then
    StepVerifier.create(updatedTimerExamMono)
      .assertNext { updatedTimerExam ->
        assertEquals(timerExam, updatedTimerExam)
      }
      .verifyComplete()
  }

  @Test
  fun `TimerExam 삭제 테스트`() {
    // Given
    val timerExamId = 1L

    `when`(timerExamRepository.deleteById(timerExamId)).thenReturn(Mono.empty<Void>())
    `when`(timerExamRepository.findById(timerExamId)).thenReturn(Mono.empty())

    // When
    val deleteResultMono = mono { timerExamService.delete(timerExamId) }

    // Then
    StepVerifier.create(deleteResultMono)
      .assertNext { deleteResult ->
        assertTrue(deleteResult)
      }
      .verifyComplete()
  }

}
