package app.klock.api.functional.user

import app.klock.api.config.TestConfig
import app.klock.api.domain.entity.User
import app.klock.api.functional.user.dto.CreateUserRequest
import app.klock.api.functional.user.dto.CreateUserResponse
import app.klock.api.service.AccountService
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
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [TestConfig::class])
@ActiveProfiles("local")
class UserRouterTest @Autowired constructor(
    private val client: WebTestClient
) {
    @MockBean
    private lateinit var accountService: AccountService

    // 테스트 데이터 설정
    val user = User(
        name = "user1",
        email = "user1@example.com",
        hashedPassword = "password",
        totalStudyTime = 0,
        userLevelId = 1,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now())

    val newUser = User(
        id = 1L,
        name = "user1",
        email = "user1@example.com",
        hashedPassword = "password",
        totalStudyTime = 0,
        userLevelId = 1,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now())

    val createUserRequest1 = CreateUserRequest(
        name = "user3",
        email = "user3@example.com",
        password = "password")
    val createUserResponse1 = CreateUserResponse(
        id = 2L,
        name = "user2",
        email = "user2@example.com"
    )

    val createUserRequest2 = CreateUserRequest(
        name = "user2",
        email = "user2@example.com",
        password = "password")
    private val users = listOf(createUserRequest1, createUserRequest2)

    @BeforeEach
    fun setUp() {
        // 테스트에 사용할 사용자 데이터를 설정합니다.
//        Mockito.`when`(userService.findAll()).thenReturn(Flux.fromIterable(users))
    }

//    @Test
//    fun `should return all users`() {
//        // userRepository.findAll() 호출 시 users 데이터 반환 설정
//        Mockito.`when`(userRepository.findAll()).thenReturn(Flux.fromIterable(users))
//
//        client.get().uri("/users")
//            .exchange()
//            .expectStatus().isOk
//            .expectBodyList(User::class.java)
//            .hasSize(2)
//            .contains(*users.toTypedArray())
//    }

    @Test
    fun `should create a user`() {
        // Save user mock
        Mockito.`when`(accountService.save(user)).thenReturn(Mono.just(newUser))

        // 요청 테스트
        // Make a request to create a user
        client.post().uri("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(createUserRequest1))
            .exchange()
            .expectStatus().isCreated
    }

}
