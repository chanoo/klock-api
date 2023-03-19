// StudySessionRouterTest.kt
package app.klock.api.router

import app.klock.api.config.TestConfig
import app.klock.api.domain.entity.StudySession
import app.klock.api.service.StudySessionService
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
import java.time.LocalDate
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [TestConfig::class])
@ActiveProfiles("test")
class StudySessionRouterTest @Autowired constructor(
    private val webTestClient: WebTestClient
) {
    @MockBean
    private lateinit var studySessionService: StudySessionService

    private lateinit var studySessions: List<StudySession>

    @BeforeEach
    fun setUp() {
        studySessions = listOf(
            StudySession(
                id = 1,
                accountId = 1,
                startTime = LocalDateTime.now(),
                endTime = LocalDateTime.now().plusHours(1)
            ),
            StudySession(
                id = 2,
                accountId = 2,
                startTime = LocalDateTime.now(),
                endTime = LocalDateTime.now().plusHours(1)
            )
        )
    }

    @Test
    fun `findByAccountIdAndStartTimeBetween`() {
        val accountId = 1L
        val startDate = LocalDate.now()

        Mockito.`when`(studySessionService.findByAccountIdAndStartTimeBetween(accountId, startDate))
            .thenReturn(Flux.fromIterable(studySessions.filter { it.accountId == accountId }))

        webTestClient.get()
            .uri("/api/study-sessions?userId=$accountId&startDate=$startDate")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(StudySession::class.java)
            .hasSize(1)
            .contains(studySessions[0])
    }

    @Test
    fun `create`() {
        val studySession =
            StudySession(accountId = 3, startTime = LocalDateTime.now(), endTime = LocalDateTime.now().plusHours(1))

        Mockito.`when`(studySessionService.create(studySession)).thenReturn(Mono.just(studySession.copy(id = 3)))

        webTestClient.post()
            .uri("/api/study-sessions")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(studySession))
            .exchange()
            .expectStatus().isCreated
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(StudySession::class.java)
            .isEqualTo(studySession.copy(id = 3))
    }

    @Test
    fun `update`() {
        val studySession = studySessions[0].copy(endTime = LocalDateTime.now().plusHours(2))

        Mockito.`when`(studySessionService.update(studySession.id!!, studySession)).thenReturn(Mono.just(studySession))

        webTestClient.put()
            .uri("/api/study-sessions/${studySession.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(studySession))
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(StudySession::class.java)
            .isEqualTo(studySession)
    }
}
