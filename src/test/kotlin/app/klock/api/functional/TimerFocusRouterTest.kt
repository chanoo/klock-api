package app.klock.api.functional

import app.klock.api.config.TestConfig
import app.klock.api.functional.timer.TimerFocusDto
import app.klock.api.service.TimerFocusService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.kotlin.anyOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime
import org.mockito.Mockito.`when` as mockitoWhen

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [TestConfig::class])
@ActiveProfiles("test")
class TimerFocusRouterTest @Autowired constructor(
  private val client: WebTestClient
) {
  @MockBean
  private lateinit var timerFocusService: TimerFocusService

  @Test
  fun `Create Focus Timer`() {
    val testTimerFocusDto = TimerFocusDto(
      userId = 2L,
      seq = 1,
      name = "Test Focus Timer"
    )

    val timerFocus = testTimerFocusDto.toDomain()
    mockitoWhen(timerFocusService.create(anyOrNull())).thenReturn(timerFocus.copy(id = 1L).toMono())
    val timerFocusDto = TimerFocusDto.from(timerFocus)

    client.post()
      .uri("/api/focus-timers")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(timerFocusDto)
      .exchange()
      .expectStatus().isCreated
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.userId").isEqualTo(timerFocusDto.userId.toInt())
      .jsonPath("$.seq").isEqualTo(timerFocusDto.seq)
      .jsonPath("$.name").isEqualTo(timerFocusDto.name)
  }

  @Test
  fun `포커스 타이머 수정`() {
    val testTimerFocusDto = TimerFocusDto(
      id = 1L,
      userId = 2L,
      seq = 1,
      name = "Test Focus Timer"
    )

    val originalTimerFocus = testTimerFocusDto.toDomain().copy(
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now()
    )

    val resTimerFocus = originalTimerFocus.copy(
      name = "Test Focus Timer Update",
      updatedAt = LocalDateTime.now()
    )
    Mockito.`when`(timerFocusService.get(anyOrNull())).thenReturn(originalTimerFocus.toMono())
    Mockito.`when`(timerFocusService.update(anyOrNull())).thenReturn(resTimerFocus.toMono())

    val updatedTimerFocusDto = TimerFocusDto.from(resTimerFocus)

    client.post()
      .uri("/api/focus-timers/${originalTimerFocus.id}")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(updatedTimerFocusDto)
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.id").isEqualTo(updatedTimerFocusDto.id!!.toInt())
      .jsonPath("$.userId").isEqualTo(updatedTimerFocusDto.userId.toInt())
      .jsonPath("$.seq").isEqualTo(updatedTimerFocusDto.seq)
      .jsonPath("$.name").isEqualTo(updatedTimerFocusDto.name)
  }

  @Test
  fun `Delete Focus Timer`() {
    val testTimerExamId = 1L

    mockitoWhen(timerFocusService.delete(testTimerExamId)).thenReturn(true.toMono())

    client.delete()
      .uri("/api/focus-timers/$testTimerExamId")
      .exchange()
      .expectStatus().isNoContent
  }

}
