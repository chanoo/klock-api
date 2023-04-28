package app.klock.api.functional

import app.klock.api.config.TestConfig
import app.klock.api.domain.entity.UserTag
import app.klock.api.service.UserTagService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [TestConfig::class])
@ActiveProfiles("test")
class UserTagRouterTest @Autowired constructor(
  private val client: WebTestClient
) {
  @MockBean
  private lateinit var userTagService: UserTagService

  // 테스트 데이터 설정
  private lateinit var userTag: UserTag

  @BeforeEach
  fun setUp() {
    // 테스트에 사용할 데이터를 설정합니다.
    userTag = UserTag(
      id = 1L,
      userId = 1L,
      tagId = 1L
    )

    // Mock the accountTagService
    Mockito.`when`(userTagService.findByUserId(1L)).thenReturn(Flux.just(userTag))
    Mockito.`when`(userTagService.create(userTag)).thenReturn(Mono.just(userTag))
  }

  @Test
  fun `계정 태그 조회`() {
    // Test the GET request to retrieve all tags for an account
    client.get().uri("/api/user-tags?userId=1")
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBodyList(UserTag::class.java)
  }

  @Test
  fun `계정 태그 생성`() {
    // Test the POST request to create a new account tag
    client.post().uri("/api/user-tags")
      .contentType(MediaType.APPLICATION_JSON)
      .body(BodyInserters.fromValue(userTag))
      .exchange()
      .expectStatus().isCreated
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody(UserTag::class.java)
  }
}
