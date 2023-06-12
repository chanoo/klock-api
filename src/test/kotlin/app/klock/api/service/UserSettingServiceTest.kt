package app.klock.api.service

import app.klock.api.domain.entity.UserSetting
import app.klock.api.repository.UserSettingRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.DayOfWeek

class UserSettingServiceTest {

    private lateinit var userSettingService: UserSettingService
    private lateinit var userSettingRepository: UserSettingRepository

    @BeforeEach
    fun setUp() {
        userSettingRepository = mockk<UserSettingRepository>()
        userSettingService = UserSettingService(userSettingRepository)
    }

    @Test
    fun `UserSetting 생성 테스트`() {
        // Given
        val userSetting = UserSetting(1L, 1L, DayOfWeek.MONDAY, 7)

        every { userSettingRepository.save(userSetting) } returns Mono.just(userSetting)

        // When
        val createdUserSetting = userSettingService.create(userSetting)

        // Then
        StepVerifier.create(createdUserSetting)
            .assertNext {createdUserSetting ->
                assertEquals(userSetting, createdUserSetting)
            }
            .verifyComplete()
    }

}