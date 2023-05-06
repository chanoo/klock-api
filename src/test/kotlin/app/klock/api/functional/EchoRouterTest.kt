package app.klock.api.functional

import app.klock.api.functional.echo.EchoDto
import app.klock.api.functional.echo.EchoHandler
import app.klock.api.functional.echo.EchoRouter
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.ServerResponse

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EchoRouterTest {

  private lateinit var echoRouter: EchoRouter
  private val echoHandler = mockk<EchoHandler>()

  private lateinit var client: WebTestClient

  @BeforeEach
  fun setUp() {
    echoRouter = EchoRouter(echoHandler)

    client = WebTestClient.bindToRouterFunction(echoRouter.echoRoutes()).build()
  }

  @Test
  fun `메시지를 그대로 반환하는 에코 엔드포인트`() {
    val message = "Hello, World!"

    coEvery { echoHandler.get(any()) } coAnswers {
      ServerResponse.ok().bodyValue(message)
    }

    client.get()
      .uri("/echo?message=$message")
      .exchange()
      .expectStatus().isOk
      .expectBody(String::class.java)
      .isEqualTo(message)
  }

  @Test
  fun `POST 메소드로 전송한 메시지에 Hello를 추가하여 반환하는 에코 엔드포인트`() {
    val message = "Hello, World!"
    val echoDto = EchoDto(message)
    val expectedResponse = echoDto.copy(message = "Hello, ${echoDto.message}!")

    coEvery { echoHandler.post(any()) } coAnswers {
      ServerResponse.ok().bodyValue(expectedResponse)
    }

    client.post()
      .uri("/echo")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(echoDto)
      .exchange()
      .expectStatus().isOk
      .expectBody(EchoDto::class.java)
      .isEqualTo(expectedResponse)
  }
}
