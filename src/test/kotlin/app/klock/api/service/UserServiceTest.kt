package app.klock.api.service

import app.klock.api.aws.s3.service.S3Service
import app.klock.api.domain.entity.*
import app.klock.api.functional.user.UpdateUserRequest
import app.klock.api.functional.user.UserInfoDto
import app.klock.api.repository.*
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ActiveProfiles
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import software.amazon.awssdk.services.s3.S3AsyncClient
import java.time.DayOfWeek
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("local")
class UserServiceTest {
  private lateinit var userService: UserService
  private lateinit var userRepository: UserRepository
  private lateinit var userLevelRepository: UserLevelRepository
  private lateinit var userSettingRepository: UserSettingRepository
  private lateinit var userTagRepository: UserTagRepository
  private lateinit var passwordEncoder: BCryptPasswordEncoder
  private lateinit var s3Service: S3Service
  private lateinit var s3AsyncClient: S3AsyncClient

  private lateinit var socialLoginRepository: SocialLoginRepository
  private lateinit var studySessionRepository: StudySessionRepository
  private lateinit var timerExamRepository: TimerExamRepository
  private lateinit var timerFocusRepository: TimerFocusRepository
  private lateinit var timerPomodoroRepository: TimerPomodoroRepository

  @BeforeEach
  fun setUp() {
    userRepository = mockk()
    userLevelRepository = mockk()
    userSettingRepository = mockk()
    userTagRepository = mockk()
    socialLoginRepository = mockk()
    studySessionRepository = mockk()
    timerExamRepository = mockk()
    timerFocusRepository = mockk()
    timerPomodoroRepository = mockk()
    passwordEncoder = mockk()
    s3AsyncClient = mockk()
    s3Service = mockk()

    val mockUserProfilePath = "mock/user/profile/path"
    val mockS3Endpoint = "mock/s3/endpoint"

    userService = UserService(
      userRepository,
      userLevelRepository,
      userSettingRepository,
      userTagRepository,
      socialLoginRepository,
      studySessionRepository,
      timerExamRepository,
      timerFocusRepository,
      timerPomodoroRepository,
      passwordEncoder,
      s3Service,
      mockUserProfilePath,
      mockS3Endpoint
    )
  }

  @Test
  fun `아이디로 사용자 조회하기`() {
    // Arrange
    val user = User(
      id = 1L,
      nickname = "user1",
      email = "user1@example.com",
      profileImage = "https://resouce.klock.app/profile-image.png",
      role = UserRole.USER,
      active = true,
      totalStudyTime = 0,
      userLevelId = 1,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now()
    )
    val userLevel = UserLevel(
      id = 1L,
      level = 1,
      requiredStudyTime = 10,
      characterName = "atom",
      characterImage = "atom.png"
    )
    val userSetting = UserSetting(
      id = 1L,
      userId = 1L,
      startOfTheWeek = DayOfWeek.MONDAY,
      startOfTheDay = 1
    )
    val userTag = UserTag(
      id = 1L,
      userId = 1L,
      tagId = 1L
    )
    val userInfoDto = UserInfoDto.from(user = user, userLevel = userLevel, userSetting = userSetting, userTag = userTag)

    every { userRepository.findById(1L) } returns Mono.just(user)
    every { userLevelRepository.findById(1L) } returns Mono.just(userLevel)
    every { userSettingRepository.findByUserId(1L) } returns Mono.just(userSetting)
    every { userTagRepository.findByUserId(1L) } returns Mono.just(userTag)

    // Act
    val foundUser = userService.findById(1L)

    // Assert
    StepVerifier.create(foundUser)
      .expectNext(userInfoDto)
      .verifyComplete()
  }

  @Test
  fun `모든 사용자 조회하기`() {
    // Arrange
    val user1 = User(
      id = 1L,
      nickname = "user1",
      email = "user1@example.com",
      role = UserRole.USER,
      active = true,
      totalStudyTime = 0,
      userLevelId = 1,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now()
    )
    val user2 = User(
      id = 2L,
      nickname = "user1",
      email = "user1@example.com",
      role = UserRole.USER,
      active = true,
      totalStudyTime = 0,
      userLevelId = 1,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now()
    )

    every { userRepository.findAll() } returns Flux.just(user1, user2)

    // Act
    val users = userService.findAll()

    // Assert
    StepVerifier.create(users)
      .expectNext(UserInfoDto.from(user1))
      .expectNext(UserInfoDto.from(user2))
      .verifyComplete()
  }

  @Test
  fun `새로운 사용자 생성하기`() {
    // Arrange
    val newUser = User(
      id = null,
      nickname = "user1",
      email = "user1@example.com",
      role = UserRole.USER,
      active = true,
      totalStudyTime = 0,
      userLevelId = 1,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now()
    )
    val savedUser = User(
      id = 1L,
      nickname = "user1",
      email = "user1@example.com",
      role = UserRole.USER,
      active = true,
      totalStudyTime = 0,
      userLevelId = 1,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now()
    )

    every { userRepository.save(newUser) } returns Mono.just(savedUser)

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
    val createdAt = LocalDateTime.now()
    val updatedAt = LocalDateTime.now()
    val existingUser = User(
      id = 1L,
      nickname = "user1",
      email = "user1@example.com",
      role = UserRole.USER,
      active = true,
      totalStudyTime = 0,
      userLevelId = 1,
      createdAt = createdAt,
      updatedAt = createdAt
    )
    val updatedUser = User(
      id = 1L,
      nickname = "new_user",
      email = "user1@example.com",
      role = UserRole.USER,
      active = true,
      totalStudyTime = 0,
      userLevelId = 1,
      createdAt = createdAt,
      updatedAt = updatedAt
    )
    val existingUserSetting = UserSetting(
      id = 1L,
      userId = 1L,
      startOfTheWeek = DayOfWeek.MONDAY,
      startOfTheDay = 1
    )
    val updateUserSetting = UserSetting(
      id = 1L,
      userId = 1L,
      startOfTheWeek = DayOfWeek.THURSDAY,
      startOfTheDay = 2
    )
    val existingUserTag = UserTag(
      id = 1L,
      userId = 1L,
      tagId = 1L
    )
    val updateUserTag = UserTag(
      id = 1L,
      userId = 1L,
      tagId = 2L
    )
    val updateUserRequest = UpdateUserRequest(
      nickname = "new_user",
      startOfTheDay = 2,
      startOfTheWeek = DayOfWeek.THURSDAY,
      tagId = 2L,
      updatedAt = updatedAt
    )
    val userInfoDto = UserInfoDto.from(user = updatedUser, userSetting = updateUserSetting, userTag = updateUserTag)

    every { userRepository.findById(1L) } returns Mono.just(existingUser)
    every { userRepository.save(any()) } returns Mono.just(updatedUser)
    every { userSettingRepository.findByUserId(1L) } returns Mono.just(existingUserSetting)
    every { userSettingRepository.save(any()) } returns Mono.just(updateUserSetting)
    every { userTagRepository.findByUserId(1L) } returns Mono.just(existingUserTag)
    every { userTagRepository.save(any()) } returns Mono.just(updateUserTag)

    // Act
    val savedUser = userService.update(1L, updateUserRequest)

    // Assert
    StepVerifier.create(savedUser)
      .expectNext(userInfoDto)
      .verifyComplete()
  }

  @Test
  fun `사용자 삭제`() {
    // Arrange
    val userId = 1L
    val user = User(
      id = userId,
      nickname = "user1",
      email = "user1@example.com",
      role = UserRole.USER,
      active = true,
      totalStudyTime = 0,
      userLevelId = 1,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now()
    )

    every { userRepository.findById(userId) } returns Mono.just(user)
    every { userRepository.save(any()) } returns Mono.just(user)
    every { socialLoginRepository.deleteByUserId(userId) } returns Mono.empty()
    every { userSettingRepository.deleteByUserId(userId) } returns Mono.empty()
    every { userTagRepository.deleteByUserId(userId) } returns Mono.empty()
    every { studySessionRepository.deleteByUserId(userId) } returns Mono.empty()
    every { timerExamRepository.deleteByUserId(userId) } returns Mono.empty()
    every { timerFocusRepository.deleteByUserId(userId) } returns Mono.empty()
    every { timerPomodoroRepository.deleteByUserId(userId) } returns Mono.empty()

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
      nickname = "user1",
      email = "user1@example.com",
      role = UserRole.USER,
      active = true,
      totalStudyTime = 0,
      userLevelId = 1,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now()
    )

    every { userRepository.findByEmail("user1@example.com") } returns Mono.just(user)

    // Act
    val foundUser = userService.findByEmail("user1@example.com")

    // Assert
    StepVerifier.create(foundUser)
      .expectNext(user)
      .verifyComplete()
  }

  @Test
  fun `비밀번호 유효성 검증`() {
    // Arrange
    val plainPassword = "password123"
    val hashedPassword = BCryptPasswordEncoder().encode(plainPassword)
    every { passwordEncoder.encode(any<String>()) } answers { BCryptPasswordEncoder().encode(firstArg()) }
    every { passwordEncoder.matches(any<String>(), any()) } answers {
      BCryptPasswordEncoder().matches(
        firstArg(),
        secondArg()
      )
    }

    // Act & Assert
    assertTrue(userService.validatePassword(plainPassword, hashedPassword))
    assertFalse(userService.validatePassword("wrong_password", hashedPassword))
  }

  @Test
  fun `비밀번호 변경`() {
    // Arrange
    val oldPassword = "old_password"
    val newPassword = "new_password"
    val hashedOldPassword = BCryptPasswordEncoder().encode(oldPassword)
    val hashedNewPassword = BCryptPasswordEncoder().encode(newPassword)
    val existingUser = User(
      id = 1L,
      nickname = "user1",
      email = "user1@example.com",
      role = UserRole.USER,
      active = true,
      totalStudyTime = 0,
      userLevelId = 1,
      hashedPassword = hashedOldPassword,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now()
    )

    every { userRepository.findById(1L) } returns Mono.just(existingUser)
    every { userRepository.save(any<User>()) } answers {
      val user = firstArg<User>()
      Mono.just(user.copy(hashedPassword = hashedNewPassword)) // 새로운 해시된 비밀번호 사용
    }
    every { passwordEncoder.encode(any<String>()) } answers { BCryptPasswordEncoder().encode(firstArg()) }
    every { passwordEncoder.matches(eq(oldPassword), eq(hashedOldPassword)) } returns true
    every { passwordEncoder.matches(eq(newPassword), eq(hashedNewPassword)) } returns true // 새 비밀번호와 새 해시가 일치하도록 설정
    every { passwordEncoder.matches(not(eq(oldPassword)), eq(hashedOldPassword)) } returns false
    every {
      passwordEncoder.matches(
        not(eq(newPassword)),
        eq(hashedNewPassword)
      )
    } returns false // 새 비밀번호가 아닌 경우 일치하지 않도록 설정

    // Act
    val savedUser = userService.changePassword(1L, oldPassword, newPassword)

    // Assert
    StepVerifier.create(savedUser)
      .assertNext { updatedUser ->
        assertEquals(existingUser.id, updatedUser.id)
        assertEquals(existingUser.nickname, updatedUser.nickname)
        assertEquals(existingUser.email, updatedUser.email)
        assertNotEquals(existingUser.hashedPassword, updatedUser.hashedPassword)
        assertTrue(passwordEncoder.matches(newPassword, updatedUser.hashedPassword))
      }
      .verifyComplete()
  }

  @Test
  fun `비밀번호 변경 - 잘못된 현재 비밀번호`() {
    // Arrange
    val correctPassword = "password123"
    val hashedCorrectPassword = BCryptPasswordEncoder().encode(correctPassword)
    val existingUser = User(
      id = 1L,
      nickname = "user1",
      email = "user1@example.com",
      role = UserRole.USER,
      active = true,
      totalStudyTime = 0,
      userLevelId = 1,
      hashedPassword = hashedCorrectPassword,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now()
    )
    every { userRepository.findById(1L) } returns Mono.just(existingUser)
    every { passwordEncoder.matches(eq(correctPassword), eq(hashedCorrectPassword)) } returns true
    every { passwordEncoder.matches(not(eq(correctPassword)), eq(hashedCorrectPassword)) } returns false

    // Act
    val savedUser = userService.changePassword(1L, "wrong_password", "new_password")

    // Assert
    StepVerifier.create(savedUser)
      .expectErrorMatches { it is IllegalArgumentException && it.message == "Invalid current password" }
      .verify()
  }

  @Test
  fun `닉네임 존재 여부 성공 체크`() {
    // Arrange
    val plainPassword = "password123"
    val hashedPassword = BCryptPasswordEncoder().encode(plainPassword)
    val nickname = "exist이름1"
    val existingUser = User(
      id = 1L,
      nickname = nickname,
      email = "user1@example.com",
      role = UserRole.USER,
      active = true,
      totalStudyTime = 0,
      userLevelId = 1,
      hashedPassword = hashedPassword,
      createdAt = LocalDateTime.now(),
      updatedAt = LocalDateTime.now()
    )
    // when은 TDD방식 given은 BDD방식
    every { userRepository.findByNickname(nickname) } returns Mono.just(existingUser)

    // Act
    val exists = userService.existedNickname(nickname)

    // Assert
    StepVerifier.create(exists)
      .assertNext { assertTrue(it) }
      .verifyComplete()
  }

}
