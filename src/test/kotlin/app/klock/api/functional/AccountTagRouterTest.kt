package app.klock.api.functional

import app.klock.api.config.TestConfig
import app.klock.api.domain.entity.AccountTag
import app.klock.api.service.AccountTagService
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
class AccountTagRouterTest @Autowired constructor(
    private val client: WebTestClient
) {
    @MockBean
    private lateinit var accountTagService: AccountTagService

    // 테스트 데이터 설정
    private lateinit var accountTag: AccountTag

    @BeforeEach
    fun setUp() {
        // 테스트에 사용할 데이터를 설정합니다.
        accountTag = AccountTag(
            id = 1L,
            accountId = 1L,
            tagId = 1L
        )

        // Mock the accountTagService
        Mockito.`when`(accountTagService.findByAccountId(1L)).thenReturn(Flux.just(accountTag))
        Mockito.`when`(accountTagService.create(accountTag)).thenReturn(Mono.just(accountTag))
    }

    @Test
    fun `계정 태그 조회`() {
        // Test the GET request to retrieve all tags for an account
        client.get().uri("/api/account-tags?accountId=1")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(AccountTag::class.java)
    }

    @Test
    fun `계정 태그 생성`() {
        // Test the POST request to create a new account tag
        client.post().uri("/api/account-tags")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(accountTag))
            .exchange()
            .expectStatus().isCreated
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(AccountTag::class.java)
    }
}
