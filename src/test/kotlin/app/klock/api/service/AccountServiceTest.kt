package app.klock.api.service

import app.klock.api.domain.entity.Account
import app.klock.api.domain.entity.AccountRole
import app.klock.api.repository.AccountRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("local")
class AccountServiceTest {

    private lateinit var accountService: AccountService

    private lateinit var accountRepository: AccountRepository

    @BeforeEach
    fun setUp() {
        accountRepository = mock(AccountRepository::class.java)
        accountService = AccountService(accountRepository)
    }

    @Test
    fun `find user by id`() {
        // Arrange
        val user = Account(
            id = 1L,
            username = "user1",
            email = "user1@example.com",
            role = AccountRole.USER,
            active = true,
            totalStudyTime = 0,
            accountLevelId = 1,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        `when`(accountRepository.findById(1L)).thenReturn(Mono.just(user))

        // Act
        val foundUser = accountService.findById(1L)

        // Assert
        StepVerifier.create(foundUser)
            .expectNext(user)
            .verifyComplete()
    }

    @Test
    fun `find all users`() {
        // Arrange
        val user1 = Account(
            id = 1L,
            username = "user1",
            email = "user1@example.com",
            role = AccountRole.USER,
            active = true,
            totalStudyTime = 0,
            accountLevelId = 1,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now())
        val user2 = Account(
            id = 2L,
            username = "user1",
            email = "user1@example.com",
            role = AccountRole.USER,
            active = true,
            totalStudyTime = 0,
            accountLevelId = 1,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now())
        `when`(accountRepository.findAll()).thenReturn(Flux.just(user1, user2))

        // Act
        val users = accountService.findAll()

        // Assert
        StepVerifier.create(users)
            .expectNext(user1)
            .expectNext(user2)
            .verifyComplete()
    }

    @Test
    fun `create user`() {
        // Arrange
        val newUser = Account(
            id = null,
            username = "user1",
            email = "user1@example.com",
            role = AccountRole.USER,
            active = true,
            totalStudyTime = 0,
            accountLevelId = 1,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now())
        val savedUser = Account(
            id = 1L,
            username = "user1",
            email = "user1@example.com",
            role = AccountRole.USER,
            active = true,
            totalStudyTime = 0,
            accountLevelId = 1,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now())
        `when`(accountRepository.save(newUser)).thenReturn(Mono.just(savedUser))

        // Act
        val createdUser = accountService.save(newUser)

        // Assert
        StepVerifier.create(createdUser)
            .expectNext(savedUser)
            .verifyComplete()
    }

    @Test
    fun `update user`() {
        // Arrange
        val existingUser = Account(
            id = 1L,
            username = "user1",
            email = "user1@example.com",
            role = AccountRole.USER,
            active = true,
            totalStudyTime = 0,
            accountLevelId = 1,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now())
        val updatedUser = Account(
            id = 1L,
            username = "user1",
            email = "user1@example.com",
            role = AccountRole.USER,
            active = true,
            totalStudyTime = 0,
            accountLevelId = 1,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now())
        `when`(accountRepository.findById(1L)).thenReturn(Mono.just(existingUser))
        `when`(accountRepository.save(updatedUser)).thenReturn(Mono.just(updatedUser))

        // Act
        val savedUser = accountService.update(1L, updatedUser)

        // Assert
        StepVerifier.create(savedUser)
            .expectNext(updatedUser)
            .verifyComplete()
    }

    @Test
    fun `delete user`() {
        // Arrange
        val userId = 1L
        `when`(accountRepository.deleteById(userId)).thenReturn(Mono.empty<Void>())

        // Act
        val deletedUser = accountService.deleteById(userId)

        // Assert
        StepVerifier.create(deletedUser)
            .verifyComplete()
    }

}
