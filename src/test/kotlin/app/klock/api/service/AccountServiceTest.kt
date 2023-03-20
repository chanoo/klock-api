package app.klock.api.service

import app.klock.api.domain.entity.Account
import app.klock.api.domain.entity.AccountRole
import app.klock.api.repository.AccountRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
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

    private lateinit var passwordEncoder: BCryptPasswordEncoder

    @BeforeEach
    fun setUp() {
        passwordEncoder = mock(BCryptPasswordEncoder::class.java)
        accountRepository = mock(AccountRepository::class.java)
        accountService = AccountService(accountRepository, passwordEncoder)
    }

    @Test
    fun `아이디로 사용자 조회하기`() {
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
    fun `모든 사용자 조회하기`() {
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
    fun `새로운 사용자 생성하기`() {
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
    fun `사용자 정보 수정하기`() {
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
    fun `사용자 삭제`() {
        // Arrange
        val userId = 1L
        `when`(accountRepository.deleteById(userId)).thenReturn(Mono.empty<Void>())

        // Act
        val deletedUser = accountService.deleteById(userId)

        // Assert
        StepVerifier.create(deletedUser)
            .verifyComplete()
    }

    @Test
    fun `이메일로 사용자 조회하기`() {
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
        `when`(accountRepository.findByEmail("user1@example.com")).thenReturn(Mono.just(user))

        // Act
        val foundUser = accountService.findByEmail("user1@example.com")

        // Assert
        StepVerifier.create(foundUser)
            .expectNext(user)
            .verifyComplete()
    }

    @Test
    fun `비밀번호 유효성 검증`() {
        // 해당 mock 객체의 메서드는 기본적으로 실제 동작을 수행하지 않습니다.
        // 이 문제를 해결하려면 passwordEncoder.encode() 메서드에 대한 반환 값을 설정해야 합니다.
        `when`(passwordEncoder.encode(anyString())).thenAnswer { invocation ->
            val argument = invocation.getArgument(0, String::class.java)
            BCryptPasswordEncoder().encode(argument)
        }
        // passwordEncoder.matches() 메서드를 사용하므로 해당 메서드에 대한 모의 동작을 설정해야 한다.
        `when`(passwordEncoder.matches(anyString(), anyString())).thenAnswer { invocation ->
            val rawPassword = invocation.getArgument(0, String::class.java)
            val encodedPassword = invocation.getArgument(1, String::class.java)
            BCryptPasswordEncoder().matches(rawPassword, encodedPassword)
        }

        // Arrange
        val plainPassword = "password123"
        val hashedPassword = passwordEncoder.encode(plainPassword)

        // Act & Assert
        assertTrue(accountService.validatePassword(plainPassword, hashedPassword))
        assertFalse(accountService.validatePassword("wrong_password", hashedPassword))
    }

    @Test
    fun `비밀번호 변경`() {
        // 해당 mock 객체의 메서드는 기본적으로 실제 동작을 수행하지 않습니다.
        // 이 문제를 해결하려면 passwordEncoder.encode() 메서드에 대한 반환 값을 설정해야 합니다.
        `when`(passwordEncoder.encode(anyString())).thenAnswer { invocation ->
            val argument = invocation.getArgument(0, String::class.java)
            BCryptPasswordEncoder().encode(argument)
        }
        // passwordEncoder.matches() 메서드를 사용하므로 해당 메서드에 대한 모의 동작을 설정해야 한다.
        `when`(passwordEncoder.matches(anyString(), anyString())).thenAnswer { invocation ->
            val rawPassword = invocation.getArgument(0, String::class.java)
            val encodedPassword = invocation.getArgument(1, String::class.java)
            BCryptPasswordEncoder().matches(rawPassword, encodedPassword)
        }

        // Arrange
        val existingUser = Account(
            id = 1L,
            username = "user1",
            email = "user1@example.com",
            role = AccountRole.USER,
            active = true,
            totalStudyTime = 0,
            accountLevelId = 1,
            hashedPassword = passwordEncoder.encode("old_password"),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        Mockito.`when`(accountRepository.findById(1L)).thenReturn(Mono.just(existingUser))
        Mockito.`when`(accountRepository.save(any(Account::class.java))).thenAnswer { invocation ->
            Mono.just(invocation.getArgument(0, Account::class.java))
        }

        // Act
        val savedUser = accountService.changePassword(1L, "old_password", "new_password")

        // Assert
        StepVerifier.create(savedUser)
            .assertNext { updatedUser ->
                assertEquals(existingUser.id, updatedUser.id)
                assertEquals(existingUser.username, updatedUser.username)
                assertEquals(existingUser.email, updatedUser.email)
                assertNotEquals(existingUser.hashedPassword, updatedUser.hashedPassword)
                assertTrue(passwordEncoder.matches("new_password", updatedUser.hashedPassword))
            }
            .verifyComplete()
    }

    @Test
    fun `비밀번호 변경 - 잘못된 현재 비밀번호`() {
        // Arrange
        val existingUser = Account(
            id = 1L,
            username = "user1",
            email = "user1@example.com",
            role = AccountRole.USER,
            active = true,
            totalStudyTime = 0,
            accountLevelId = 1,
            hashedPassword = passwordEncoder.encode("old_password"),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val updatedUser = existingUser.copy(hashedPassword = passwordEncoder.encode("new_password"))
        Mockito.`when`(accountRepository.findById(1L)).thenReturn(Mono.just(existingUser))

        // Act
        val savedUser = accountService.changePassword(1L, "wrong_password", "new_password")

        // Assert
        StepVerifier.create(savedUser)
            .expectErrorMatches { it is IllegalArgumentException && it.message == "Invalid current password" }
            .verify()
    }

}
