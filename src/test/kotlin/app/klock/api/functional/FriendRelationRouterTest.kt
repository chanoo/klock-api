package app.klock.api.functional

import app.klock.api.config.TestConfig
import app.klock.api.domain.entity.FriendRelation
import app.klock.api.service.FriendRelationService
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
class FriendRelationRouterTest @Autowired constructor(
    private val client: WebTestClient
) {
    @MockBean
    private lateinit var friendRelationService: FriendRelationService

    lateinit var friendRelation: FriendRelation

    @BeforeEach
    fun setUp() {
        friendRelation = FriendRelation(id = 1L, requesterId = 1L, friendId = 2L)
    }

    @Test
    fun `친구 관계 생성`() {
        val createFriendRelationRequest = mapOf("requesterId" to 1L, "addresseeId" to 2L)

        Mockito.`when`(friendRelationService.create(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Mono.just(friendRelation))

        client.post().uri("/api/friend-relations")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(createFriendRelationRequest))
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("$.requesterId").isEqualTo(1L)
            .jsonPath("$.friendId").isEqualTo(2L)
    }

    @Test
    fun `요청자 ID로 친구 관계 조회`() {
        val friendRelations = listOf(
            FriendRelation(id = 1L, requesterId = 1L, friendId = 2L),
            FriendRelation(id = 2L, requesterId = 1L, friendId = 3L)
        )

        Mockito.`when`(friendRelationService.getFriendRelationsByRequesterId(Mockito.anyLong())).thenReturn(Flux.fromIterable(friendRelations))

        client.get().uri("/api/friend-relations?requesterId=1")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].requesterId").isEqualTo(1L)
            .jsonPath("$[0].friendId").isEqualTo(2L)
            .jsonPath("$[1].requesterId").isEqualTo(1L)
            .jsonPath("$[1].friendId").isEqualTo(3L)
    }
}
