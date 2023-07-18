package app.klock.api.service

import app.klock.api.domain.entity.TimerAuto
import app.klock.api.domain.entity.TimerFocus
import app.klock.api.repository.TimerAutoRepository
import app.klock.api.repository.TimerFocusRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class TimerAutoServiceTest {

  private lateinit var timerAutoService: TimerAutoService
  private lateinit var timerAutoRepository: TimerAutoRepository

  @BeforeEach
  fun setUp() {
    timerAutoRepository = mockk()
    timerAutoService = TimerAutoService(timerAutoRepository)
  }

  @Test
  fun `TimerAuto 생성 테스트`() {
    // Given
    val timerAuto = TimerAuto(1L, 1, 1, "Auto 1")

    every { timerAutoRepository.save(timerAuto) } returns Mono.just(timerAuto)

    // When
    val createdTimerAutoMono = timerAutoService.create(timerAuto)

    // Then
    StepVerifier.create(createdTimerAutoMono)
      .assertNext { createdTimerAuto ->
        assertEquals(timerAuto, createdTimerAuto)
      }
      .verifyComplete()
  }

  @Test
  fun `TimerAuto 조회 테스트`() {
    // Given
    val timerAutoId = 1L
    val timerAuto = TimerAuto(timerAutoId, 1, 1, "Auto 1")

    every { timerAutoRepository.findById(timerAutoId) } returns Mono.just(timerAuto)

    // When
    val foundTimerAutoMono = timerAutoService.get(timerAutoId)

    // Then
    StepVerifier.create(foundTimerAutoMono)
      .assertNext { foundTimerAuto ->
        assertEquals(timerAuto, foundTimerAuto)
      }
      .verifyComplete()
  }

  @Test
  fun `TimerAuto 업데이트 테스트`() {
    // Given
    val timerAuto = TimerAuto(1L, 1, 1, "Auto 1 Updated")

    every { timerAutoRepository.save(timerAuto) } returns Mono.just(timerAuto)

    // When
    val updatedTimerAutoMono = timerAutoService.update(timerAuto)

    // Then
    StepVerifier.create(updatedTimerAutoMono)
      .assertNext { updatedTimerAuto ->
        assertEquals(timerAuto, updatedTimerAuto)
      }
      .verifyComplete()
  }

  @Test
  fun `TimerAuto 삭제 테스트`() {
    // Given
    val timerAutoId = 1L

    every { timerAutoRepository.deleteById(timerAutoId) } returns Mono.empty<Void>()
    every { timerAutoRepository.findById(timerAutoId) } returns Mono.empty()

    // When
    val deleteResultMono = timerAutoService.delete(timerAutoId)

    // Then
    StepVerifier.create(deleteResultMono)
      .assertNext { deleteResult ->
        assertTrue(deleteResult)
      }
      .verifyComplete()
  }
}
