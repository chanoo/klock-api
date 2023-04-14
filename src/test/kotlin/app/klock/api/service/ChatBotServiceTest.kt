import app.klock.api.domain.entity.ChatBot
import app.klock.api.repository.ChatBotRepository
import app.klock.api.service.ChatBotService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class ChatBotServiceTest {

  @Mock
  private lateinit var chatBotRepository: ChatBotRepository

  private lateinit var chatBotService: ChatBotService

  @BeforeEach
  fun setUp() {
    chatBotService = ChatBotService(chatBotRepository);
  }

  @Test
  fun `getByActiveChatBots should return active chatbots`() {
    val currentTime = LocalDateTime.now()
    val activeChatBots = listOf(
      ChatBot(1, "Subject 1", "Active ChatBot 1", "Title 1", "Persona 1", true, currentTime, currentTime),
      ChatBot(2, "Subject 2", "Active ChatBot 2", "Title 2", "Persona 2", true, currentTime, currentTime)
    )

    Mockito.`when`(chatBotRepository.findByActive(true)).thenReturn(Flux.fromIterable(activeChatBots))

    val result = chatBotService.getByActiveChatBots(true)

    StepVerifier.create(result)
      .expectNext(activeChatBots[0])
      .expectNext(activeChatBots[1])
      .verifyComplete()

    Mockito.verify(chatBotRepository).findByActive(true)
  }

  @Test
  fun `getByActiveChatBots should return inactive chatbots`() {
    val currentTime = LocalDateTime.now()
    val inactiveChatBots = listOf(
      ChatBot(3, "Subject 3", "Inactive ChatBot 1", "Title 3", "Persona 3", false, currentTime, currentTime),
      ChatBot(4, "Subject 4", "Inactive ChatBot 2", "Title 4", "Persona 4", false, currentTime, currentTime)
    )

    Mockito.`when`(chatBotRepository.findByActive(false)).thenReturn(Flux.fromIterable(inactiveChatBots))

    val result = chatBotService.getByActiveChatBots(false)

    StepVerifier.create(result)
      .expectNext(inactiveChatBots[0])
      .expectNext(inactiveChatBots[1])
      .verifyComplete()

    Mockito.verify(chatBotRepository).findByActive(false)
  }
}
