package app.klock.api.service

import app.klock.api.domain.entity.TimerFocus
import app.klock.api.repository.TimerFocusRepository
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
class TimerFocusServiceTest {

  private lateinit var timerFocusService: TimerFocusService
  private lateinit var timerFocusRepository: TimerFocusRepository

  @BeforeEach
  fun setUp() {
    timerFocusRepository = Mockito.mock(TimerFocusRepository::class.java)
    timerFocusService = TimerFocusService(timerFocusRepository)
  }

  @Test
  fun `TimerFocus 생성 테스트`() {
    // Given
    val timerFocus = TimerFocus(1L, 1, 1, "Focus 1")

    `when`(timerFocusRepository.save(timerFocus)).thenReturn(Mono.just(timerFocus))

    // When
    val createdTimerFocusMono = mono { timerFocusService.create(timerFocus) }

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

    `when`(timerFocusRepository.findById(timerFocusId)).thenReturn(Mono.just(timerFocus))

    // When
    val foundTimerFocusMono = mono { timerFocusService.get(timerFocusId) }

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

    `when`(timerFocusRepository.save(timerFocus)).thenReturn(Mono.just(timerFocus))

    // When
    val updatedTimerFocusMono = mono { timerFocusService.update(timerFocus) }

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

    `when`(timerFocusRepository.deleteById(timerFocusId)).thenReturn(Mono.empty<Void>())
    `when`(timerFocusRepository.findById(timerFocusId)).thenReturn(Mono.empty())

    // When
    val deleteResultMono = mono { timerFocusService.delete(timerFocusId) }

    // Then
    StepVerifier.create(deleteResultMono)
      .assertNext { deleteResult ->
        assertTrue(deleteResult)
      }
      .verifyComplete()
  }
}
