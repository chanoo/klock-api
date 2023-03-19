package app.klock.api.functional

import app.klock.api.config.TestConfig
import app.klock.api.domain.entity.Tag
import app.klock.api.service.TagService
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
class TagRouterTest @Autowired constructor(
    private val client: WebTestClient
) {
    @MockBean
    private lateinit var tagService: TagService

    private lateinit var tag1: Tag
    private lateinit var tag2: Tag

    @BeforeEach
    fun setUp() {
        tag1 = Tag(name = "Tag1")
        tag2 = Tag(name = "Tag2")
    }

    @Test
    fun `전체 태그 조회`() {
        Mockito.`when`(tagService.findAll()).thenReturn(Flux.just(tag1, tag2))

        client.get().uri("/api/tags")
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
        Mockito.`when`(tagService.create(newTag)).thenReturn(Mono.just(newTag.copy(id = 3L)))

        client.post().uri("/api/tags")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(newTag))
            .exchange()
            .expectStatus().isCreated
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.name").isEqualTo("NewTag")
    }

    @Test
    fun `태그 조회`() {
        val tag = Tag(id = 1L, name = "Tag1")
        Mockito.`when`(tagService.findById(tag.id!!)).thenReturn(Mono.just(tag))

        client.get().uri("/api/tags/${tag.id}")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.name").isEqualTo("Tag1")
    }
    @Test
    fun `태그 업데이트`() {
        val tag = Tag(id = 1L, name = "Original Tag")
        val updatedTag = Tag(id = 1L, name = "UpdatedTag")
        Mockito.`when`(tagService.update(tag.id!!, updatedTag)).thenReturn(Mono.just(tag.copy(name = "UpdatedTag")))

        client.put().uri("/api/tags/${tag.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(updatedTag))
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.name").isEqualTo("UpdatedTag")
    }

    @Test
    fun `태그 삭제`() {
        val deleteTag = Tag(id = 1L, name = "UpdatedTag")
        Mockito.`when`(tagService.deleteById(deleteTag.id!!)).thenReturn(Mono.empty<Void>())

        client.delete().uri("/api/tags/${deleteTag.id}")
            .exchange()
            .expectStatus().isNoContent
    }
}
