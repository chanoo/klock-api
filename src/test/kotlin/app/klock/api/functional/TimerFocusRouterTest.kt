package app.klock.api.functional

import app.klock.api.functional.timer.*
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.ServerResponse

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TimerFocusRouterTest {

  private lateinit var timerFocusRouter: TimerFocusRouter
  private val timerFocusHandler = mockk<TimerFocusHandler>()

  @BeforeEach
  fun setUp() {
    timerFocusRouter = TimerFocusRouter(timerFocusHandler)
  }


  @Test
  fun `POST 요청으로 Focus 타이머 생성 테스트`() {
    val timerFocusDto = TimerFocusDto(
      userId = 2L,
      seq = 1,
      name = "Test Focus Timer"
    )

    val createdTimerFocusDto = timerFocusDto.copy(id = 1L)

    coEvery { timerFocusHandler.createFocusTimer(any()) } coAnswers {
      ServerResponse.status(HttpStatus.CREATED).bodyValue(createdTimerFocusDto)
    }

    val client = WebTestClient.bindToRouterFunction(timerFocusRouter.timerFocusRoutes()).build()

    client.post()
      .uri("/api/focus-timers")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(timerFocusDto)
      .exchange()
      .expectBody(TimerFocusDto::class.java)
      .value { actualTimerFocusDto ->
        Assertions.assertEquals(1L, actualTimerFocusDto.id, "ID가 1이어야 합니다.")
        Assertions.assertEquals(timerFocusDto.userId, actualTimerFocusDto.userId)
        Assertions.assertEquals(timerFocusDto.seq, actualTimerFocusDto.seq)
        Assertions.assertEquals(timerFocusDto.type, actualTimerFocusDto.type)
        Assertions.assertEquals(timerFocusDto.name, actualTimerFocusDto.name)
      }
  }

  @Test
  fun `POST 요청으로 Focus 타이머 수정 테스트`() {
    val timerId = 1L
    val timerFocusDto = TimerFocusDto(
      userId = 2L,
      seq = 1,
      name = "Test Focus Timer Update"
    )

    coEvery { timerFocusHandler.updateFocusTimer(any()) } coAnswers {
      ServerResponse.ok().bodyValue(timerFocusDto)
    }

    val client = WebTestClient.bindToRouterFunction(timerFocusRouter.timerFocusRoutes()).build()

    client.post()
      .uri("/api/focus-timers/$timerId")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(timerFocusDto)
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.OK)
      .expectBody(TimerFocusDto::class.java)
      .isEqualTo(timerFocusDto)
  }

  @Test
  fun `DELETE 요청으로 Focus 타이머 삭제 테스트`() {
    val timerId = 1L

    coEvery { timerFocusHandler.deleteFocusTimer(any()) } coAnswers {
      ServerResponse.noContent().build()
    }

    val client = WebTestClient.bindToRouterFunction(timerFocusRouter.timerFocusRoutes()).build()

    client.delete()
      .uri("/api/focus-timers/$timerId")
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.NO_CONTENT)
  }

}
