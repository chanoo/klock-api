package app.klock.api.functional

import app.klock.api.config.TestConfig
import app.klock.api.domain.entity.FriendRelation
import app.klock.api.functional.friendrelation.FriendRelationHandler
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerResponse

@ActiveProfiles("test")
@SpringBootTest(classes = [TestConfig::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FriendRelationRouterTest {

  private lateinit var friendRelationRouter: FriendRelationRouter
  private val friendRelationHandler = mockk<FriendRelationHandler>()

  private lateinit var client: WebTestClient

  @BeforeEach
  fun setUp() {
    friendRelationRouter = FriendRelationRouter(friendRelationHandler)

    client = WebTestClient.bindToRouterFunction(friendRelationRouter.friendRelationRoutes()).build()
  }

  @Test
  fun `친구 관계 생성`() {
    val createFriendRelationRequest = mapOf("requesterId" to 1L, "addresseeId" to 2L)

    coEvery { friendRelationHandler.create(any()) } coAnswers {
      ServerResponse.status(201).bodyValue(createFriendRelationRequest)
    }

    client.post().uri("/api/v1/friend-relations")
      .contentType(MediaType.APPLICATION_JSON)
      .body(BodyInserters.fromValue(createFriendRelationRequest))
      .exchange()
      .expectStatus().isCreated
      .expectBody()
      .jsonPath("$.requesterId").isEqualTo(1L)
  }

  @Test
  fun `요청자 ID로 친구 관계 조회`() {
    val friendRelations = listOf(
      FriendRelation(id = 1L, requesterId = 1L, friendId = 2L),
      FriendRelation(id = 2L, requesterId = 1L, friendId = 3L)
    )

    coEvery { friendRelationHandler.getFriendRelationsByRequesterId(any()) } coAnswers {
      ServerResponse.ok().bodyValue(friendRelations)
    }

    client.get().uri("/api/v1/friend-relations?requesterId=1")
      .exchange()
      .expectStatus().isOk
      .expectBody()
      .jsonPath("$[0].requesterId").isEqualTo(1L)
      .jsonPath("$[0].friendId").isEqualTo(2L)
      .jsonPath("$[1].requesterId").isEqualTo(1L)
      .jsonPath("$[1].friendId").isEqualTo(3L)
  }
}
