package app.klock.api.functional

import app.klock.api.config.TestConfig
import app.klock.api.domain.entity.ChatBot
import app.klock.api.service.ChatBotService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import java.time.LocalDateTime
import org.mockito.Mockito.`when` as mockitoWhen

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [TestConfig::class])
@ActiveProfiles("test")
class ChatBotRouterTest @Autowired constructor(
  private val client: WebTestClient
) {
  @MockBean
  private lateinit var chatBotService: ChatBotService

  @Test
  fun `챗봇 목록 가져오기`() {
    // Prepare test data
    val testChatBots = listOf(
      ChatBot(
        id = 1L,
        subject = "국어",
        title = "문장의 비결, 국어 전문가 미희 선생님",
        name = "미희",
        persona = "안녕하세요, 국어 선생님 미희입니다! 맞춤법, 작문, 독해 등 국어의 모든 것을 함께 배워볼까요? 궁금한 것이 있다면 언제든지 질문해주세요!",
        active = true,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
      )
      // 추가적인 테스트 데이터를 여기에 추가할 수 있습니다.
    )

    // Mock the chatBotService
    mockitoWhen(chatBotService.getByActiveChatBots(true)).thenReturn(Flux.fromIterable(testChatBots))

    // Test the GET API
    client.get()
      .uri("/api/chatbots?active=true")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$[0].id").isEqualTo(testChatBots[0].id!!.toInt())
      .jsonPath("$[0].subject").isEqualTo(testChatBots[0].subject)
      .jsonPath("$[0].title").isEqualTo(testChatBots[0].title)
      .jsonPath("$[0].name").isEqualTo(testChatBots[0].name)
      .jsonPath("$[0].persona").isEqualTo(testChatBots[0].persona)
  }
}
