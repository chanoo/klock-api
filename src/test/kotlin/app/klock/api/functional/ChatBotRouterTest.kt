package app.klock.api.functional

import app.klock.api.functional.chatBot.ChatBotDTO
import app.klock.api.functional.chatBot.ChatBotRouter
import app.klock.api.handler.ChatBotHandler
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.ServerResponse

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatBotRouterTest {

  private lateinit var chatbotRouter: ChatBotRouter
  private val chatBotHandler = mockk<ChatBotHandler>()

  private lateinit var client: WebTestClient

  @BeforeEach
  fun setUp() {
    chatbotRouter = ChatBotRouter(chatBotHandler)

    client = WebTestClient.bindToRouterFunction(chatbotRouter.chatBotRoutes()).build()
  }

  @Test
  fun `챗봇 목록 가져오기`() {

    val testChatBots = listOf(
      ChatBotDTO(
        id = 1L,
        subject = "국어",
        title = "문장의 비결, 국어 전문가 미희 선생님",
        name = "미희",
        chatBotImageUrl = "img_korean_teacher",
        persona = "안녕하세요, 국어 선생님 미희입니다! 맞춤법, 작문, 독해 등 국어의 모든 것을 함께 배워볼까요? 궁금한 것이 있다면 언제든지 질문해주세요!",
      )
    )

    coEvery { chatBotHandler.getByActiveChatBots(any()) } coAnswers {
      ServerResponse.ok().bodyValue(testChatBots)
    }

    // Test the GET API
    client.get()
      .uri("/api/chat-bots?active=true")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectBody(object : ParameterizedTypeReference<List<ChatBotDTO>>() {})
      .value { actualChatBots ->
        val actualChatBotDto = actualChatBots[0]

        assertEquals(testChatBots[0].id, actualChatBotDto.id)
        assertEquals(testChatBots[0].subject, actualChatBotDto.subject)
        assertEquals(testChatBots[0].title, actualChatBotDto.title)
        assertEquals(testChatBots[0].name, actualChatBotDto.name)
        assertEquals(testChatBots[0].chatBotImageUrl, actualChatBotDto.chatBotImageUrl)
        assertEquals(testChatBots[0].persona, actualChatBotDto.persona)
      }
  }
}
