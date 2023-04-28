package app.klock.api.service

import app.klock.api.domain.entity.User
import app.klock.api.domain.entity.UserRole
import app.klock.api.repository.UserRepository
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
class UserServiceTest {

  private lateinit var userService: UserService

  private lateinit var userRepository: UserRepository

  private lateinit var passwordEncoder: BCryptPasswordEncoder

  @BeforeEach
  fun setUp() {
    passwordEncoder = mock(BCryptPasswordEncoder::class.java)
    userRepository = mock(UserRepository::class.java)
    userService = UserService(userRepository, passwordEncoder)
  }

  @Test
  fun `아이디로 사용자 조회하기`() {
    // Arrange
    val user = User(
      id = 1L,
      username = "user1",
      email = "user1@example.com",
      role = UserRole.USER,
      active = true,
      totalStudyTime = 0,
      userLevelId = 1,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now()
    )
    `when`(userRepository.findById(1L)).thenReturn(Mono.just(user))

    // Act
    val foundUser = userService.findById(1L)

    // Assert
    StepVerifier.create(foundUser)
      .expectNext(user)
      .verifyComplete()
  }

  @Test
  fun `모든 사용자 조회하기`() {
    // Arrange
    val user1 = User(
      id = 1L,
      username = "user1",
      email = "user1@example.com",
      role = UserRole.USER,
      active = true,
      totalStudyTime = 0,
      userLevelId = 1,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now())
    val user2 = User(
      id = 2L,
      username = "user1",
      email = "user1@example.com",
      role = UserRole.USER,
      active = true,
      totalStudyTime = 0,
      userLevelId = 1,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now())
    `when`(userRepository.findAll()).thenReturn(Flux.just(user1, user2))

    // Act
    val users = userService.findAll()

    // Assert
    StepVerifier.create(users)
      .expectNext(user1)
      .expectNext(user2)
      .verifyComplete()
  }

  @Test
  fun `새로운 사용자 생성하기`() {
    // Arrange
    val newUser = User(
      id = null,
      username = "user1",
      email = "user1@example.com",
      role = UserRole.USER,
      active = true,
      totalStudyTime = 0,
      userLevelId = 1,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now())
    val savedUser = User(
      id = 1L,
      username = "user1",
      email = "user1@example.com",
      role = UserRole.USER,
      active = true,
      totalStudyTime = 0,
      userLevelId = 1,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now())
    `when`(userRepository.save(newUser)).thenReturn(Mono.just(savedUser))

    // Act
    val createdUser = userService.save(newUser)

    // Assert
    StepVerifier.create(createdUser)
      .expectNext(savedUser)
      .verifyComplete()
  }

  @Test
  fun `사용자 정보 수정하기`() {
    // Arrange
    val existingUser = User(
      id = 1L,
      username = "user1",
      email = "user1@example.com",
      role = UserRole.USER,
      active = true,
      totalStudyTime = 0,
      userLevelId = 1,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now())
    val updatedUser = User(
      id = 1L,
      username = "user1",
      email = "user1@example.com",
      role = UserRole.USER,
      active = true,
      totalStudyTime = 0,
      userLevelId = 1,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now())
    `when`(userRepository.findById(1L)).thenReturn(Mono.just(existingUser))
    `when`(userRepository.save(updatedUser)).thenReturn(Mono.just(updatedUser))

    // Act
    val savedUser = userService.update(1L, updatedUser)

    // Assert
    StepVerifier.create(savedUser)
      .expectNext(updatedUser)
      .verifyComplete()
  }

  @Test
  fun `사용자 삭제`() {
    // Arrange
    val userId = 1L
    `when`(userRepository.deleteById(userId)).thenReturn(Mono.empty<Void>())

    // Act
    val deletedUser = userService.deleteById(userId)

    // Assert
    StepVerifier.create(deletedUser)
      .verifyComplete()
  }

  @Test
  fun `이메일로 사용자 조회하기`() {
    // Arrange
    val user = User(
      id = 1L,
      username = "user1",
      email = "user1@example.com",
      role = UserRole.USER,
      active = true,
      totalStudyTime = 0,
      userLevelId = 1,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now()
    )
    `when`(userRepository.findByEmail("user1@example.com")).thenReturn(Mono.just(user))

    // Act
    val foundUser = userService.findByEmail("user1@example.com")

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
    assertTrue(userService.validatePassword(plainPassword, hashedPassword))
    assertFalse(userService.validatePassword("wrong_password", hashedPassword))
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
    val existingUser = User(
      id = 1L,
      username = "user1",
      email = "user1@example.com",
      role = UserRole.USER,
      active = true,
      totalStudyTime = 0,
      userLevelId = 1,
      hashedPassword = passwordEncoder.encode("old_password"),
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now()
    )
    Mockito.`when`(userRepository.findById(1L)).thenReturn(Mono.just(existingUser))
    Mockito.`when`(userRepository.save(any(User::class.java))).thenAnswer { invocation ->
      Mono.just(invocation.getArgument(0, User::class.java))
    }

    // Act
    val savedUser = userService.changePassword(1L, "old_password", "new_password")

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
    val existingUser = User(
      id = 1L,
      username = "user1",
      email = "user1@example.com",
      role = UserRole.USER,
      active = true,
      totalStudyTime = 0,
      userLevelId = 1,
      hashedPassword = passwordEncoder.encode("old_password"),
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now()
    )
    val updatedUser = existingUser.copy(hashedPassword = passwordEncoder.encode("new_password"))
    Mockito.`when`(userRepository.findById(1L)).thenReturn(Mono.just(existingUser))

    // Act
    val savedUser = userService.changePassword(1L, "wrong_password", "new_password")

    // Assert
    StepVerifier.create(savedUser)
      .expectErrorMatches { it is IllegalArgumentException && it.message == "Invalid current password" }
      .verify()
  }

}
