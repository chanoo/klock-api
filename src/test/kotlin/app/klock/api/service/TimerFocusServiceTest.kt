package app.klock.api.service

import app.klock.api.domain.entity.TimerFocus
import app.klock.api.repository.TimerFocusRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class TimerFocusServiceTest {

  private lateinit var timerFocusService: TimerFocusService
  private lateinit var timerFocusRepository: TimerFocusRepository

  @BeforeEach
  fun setUp() {
    timerFocusRepository = mockk()
    timerFocusService = TimerFocusService(timerFocusRepository)
  }

  @Test
  fun `TimerFocus 생성 테스트`() {
    // Given
    val timerFocus = TimerFocus(1L, 1, 1, "Focus 1")

    every { timerFocusRepository.save(timerFocus) } returns Mono.just(timerFocus)

    // When
    val createdTimerFocusMono = timerFocusService.create(timerFocus)

    // Then
    StepVerifier.create(createdTimerFocusMono)
      .assertNext { createdTimerFocus ->
        assertEquals(timerFocus, createdTimerFocus)
      }
      .verifyComplete()
  }

  @Test
  fun `TimerFocus 조회 테스트`() {
    // Given
    val timerFocusId = 1L
    val timerFocus = TimerFocus(timerFocusId, 1, 1, "Focus 1")

    every { timerFocusRepository.findById(timerFocusId) } returns Mono.just(timerFocus)

    // When
    val foundTimerFocusMono = timerFocusService.get(timerFocusId)

    // Then
    StepVerifier.create(foundTimerFocusMono)
      .assertNext { foundTimerFocus ->
        assertEquals(timerFocus, foundTimerFocus)
      }
      .verifyComplete()
  }

  @Test
  fun `TimerFocus 업데이트 테스트`() {
    // Given
    val timerFocus = TimerFocus(1L, 1, 1, "Focus 1 Updated")

    every { timerFocusRepository.save(timerFocus) } returns Mono.just(timerFocus)

    // When
    val updatedTimerFocusMono = timerFocusService.update(timerFocus)

    // Then
    StepVerifier.create(updatedTimerFocusMono)
      .assertNext { updatedTimerFocus ->
        assertEquals(timerFocus, updatedTimerFocus)
      }
      .verifyComplete()
  }

  @Test
  fun `TimerFocus 삭제 테스트`() {
    // Given
    val timerFocusId = 1L

    every { timerFocusRepository.deleteById(timerFocusId) } returns Mono.empty<Void>()
    every { timerFocusRepository.findById(timerFocusId) } returns Mono.empty()

    // When
    val deleteResultMono = timerFocusService.delete(timerFocusId)

    // Then
    StepVerifier.create(deleteResultMono)
      .assertNext { deleteResult ->
        assertTrue(deleteResult)
      }
      .verifyComplete()
  }
}
