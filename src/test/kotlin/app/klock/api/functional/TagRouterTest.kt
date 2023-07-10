package app.klock.api.functional

import app.klock.api.domain.entity.Tag
import app.klock.api.functional.tag.TagHandler
import app.klock.api.functional.tag.TagRouter
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
class TagRouterTest {

  private lateinit var tagRouter: TagRouter
  private val tagHandler = mockk<TagHandler>()

  private lateinit var client: WebTestClient

  private lateinit var tag1: Tag
  private lateinit var tag2: Tag

  @BeforeEach
  fun setUp() {
    tagRouter = TagRouter(tagHandler)

    client = WebTestClient.bindToRouterFunction(tagRouter.tagRoutes()).build()

    tag1 = Tag(id = 1L, name = "Tag1")
    tag2 = Tag(id = 2L, name = "Tag2")
  }

  @Test
  fun `전체 태그 조회`() {

    coEvery { tagHandler.getall(any()) } coAnswers {
      ServerResponse.ok().bodyValue(listOf(tag1, tag2))
    }

    client.get().uri("/api/v1/tags")
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.[0].name").isEqualTo("Tag1")
      .jsonPath("$.[1].name").isEqualTo("Tag2")
  }

  @Test
  fun `태그 생성`() {
    val newTag = Tag(name = "NewTag")

    coEvery { tagHandler.create(any()) } coAnswers {
      ServerResponse.status(201).bodyValue(tag1.copy(name = "NewTag"))
    }

    client.post().uri("/api/v1/tags")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(newTag)
      .exchange()
      .expectStatus().isCreated
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.name").isEqualTo("NewTag")
  }

  @Test
  fun `태그 조회`() {

    coEvery { tagHandler.get(any()) } coAnswers {
      ServerResponse.ok().bodyValue(tag1)
    }

    client.get().uri("/api/v1/tags/${tag1.id}")
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.name").isEqualTo("Tag1")
  }

  @Test
  fun `태그 업데이트`() {
    val updatedTag = Tag(id = 1L, name = "UpdatedTag")

    coEvery { tagHandler.update(any()) } coAnswers {
      ServerResponse.ok().bodyValue(tag1.copy(name = "UpdatedTag"))
    }

    client.put().uri("/api/v1/tags/${updatedTag.id}")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(updatedTag)
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.name").isEqualTo("UpdatedTag")
  }

  @Test
  fun `태그 삭제`() {
    val tagId = 1L

    coEvery { tagHandler.delete(any()) } coAnswers {
      ServerResponse.noContent().build()
    }

    client.delete().uri("/api/v1/tags/${tagId}")
      .exchange()
      .expectStatus().isNoContent
  }

}