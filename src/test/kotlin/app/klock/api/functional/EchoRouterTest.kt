package app.klock.api.functional

import app.klock.api.config.TestConfig
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
}
