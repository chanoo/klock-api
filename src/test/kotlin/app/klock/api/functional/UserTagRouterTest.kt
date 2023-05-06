package app.klock.api.functional

import app.klock.api.domain.entity.UserTag
import app.klock.api.functional.userTag.UserTagHandler
import app.klock.api.functional.userTag.UserTagRouter
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerResponse

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserTagRouterTest {

  private lateinit var userTagRouter: UserTagRouter
  private val userTagHandler = mockk<UserTagHandler>()
  
  private lateinit var userTag: UserTag
  private lateinit var client: WebTestClient

  @BeforeEach
  fun setUp() {
    userTagRouter = UserTagRouter(userTagHandler)

    userTagRouter = UserTagRouter(userTagHandler)
    userTag = UserTag(
      id = 1L,
      userId = 1L,
      tagId = 1L
    )

    client = WebTestClient.bindToRouterFunction(userTagRouter.userTagRoutes()).build()
  }

  @Test
  fun `계정 태그 조회`() {

    coEvery { userTagHandler.getUserTags(any()) } coAnswers {
      ServerResponse.ok().bodyValue(userTag)
    }

    client.get().uri("/api/user-tags?userId=1")
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBodyList(UserTag::class.java)
  }

  @Test
  fun `계정 태그 생성`() {

    coEvery { userTagHandler.create(any()) } coAnswers {
      ServerResponse.status(HttpStatus.CREATED).bodyValue(userTag)
    }

    client.post().uri("/api/user-tags")
      .contentType(MediaType.APPLICATION_JSON)
      .body(BodyInserters.fromValue(userTag))
      .exchange()
      .expectStatus().isCreated
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody(UserTag::class.java)
  }
}
