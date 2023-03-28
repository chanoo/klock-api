package app.klock.api.functional

import app.klock.api.config.TestConfig
import app.klock.api.functional.echo.EchoDto
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [TestConfig::class])
@ActiveProfiles("test")
class EchoRouterTest @Autowired constructor(
    private val client: WebTestClient
) {
    @Test
    fun `메시지를 그대로 반환하는 에코 엔드포인트`() {
        val message = "Hello, World!"

        client.get()
            .uri("/echo?message=$message")
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .isEqualTo(message)
    }

    @Test
    fun `메시지가 누락된 경우 에코 엔드포인트가 잘못된 요청을 반환`() {
        client.get()
            .uri("/echo")
            .exchange()
            .expectStatus().is5xxServerError
    }

    @Test
    fun `POST 메소드로 전송한 메시지에 Hello를 추가하여 반환하는 에코 엔드포인트`() {
        val message = "Hello, World!"
        val echoDto = EchoDto(message)
        val expectedResponse = echoDto.copy(message = "Hello, ${echoDto.message}!")

        client.post()
            .uri("/echo")
            .bodyValue(echoDto)
            .exchange()
            .expectStatus().isOk
            .expectBody(EchoDto::class.java)
            .isEqualTo(expectedResponse)
    }
}
