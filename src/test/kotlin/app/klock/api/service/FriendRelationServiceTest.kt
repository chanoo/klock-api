package app.klock.api.service

import app.klock.api.domain.entity.FriendRelation
import app.klock.api.domain.entity.User
import app.klock.api.domain.entity.UserRole
import app.klock.api.functional.friendRelation.FriendDetailDto
import app.klock.api.functional.friendRelation.FriendRelationDto
import app.klock.api.repository.FriendRelationNativeSqlRepository
import app.klock.api.repository.FriendRelationRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
@SpringBootTest(classes = [FriendRelationService::class])
class FriendRelationServiceTest @Autowired constructor(
    private val friendRelationService: FriendRelationService
) {

    @MockBean
    private lateinit var friendRelationRepository: FriendRelationRepository
    @MockBean
    private lateinit var friendRelationNativeSqlRepository: FriendRelationNativeSqlRepository

    @BeforeEach
    fun setUp() {
        Mockito.`when`(friendRelationNativeSqlRepository.findFriendDetails(anyLong()))
            .thenReturn(Flux.empty())
    }

    @Test
    fun `팔로우 요청`() {
        val friendRelation = FriendRelation(userId = 1L, followId = 2L)
        val savedFriendRelation = friendRelation.copy(id = 1L)
        val followUser = User(id = 2L, email = "a@a.com", nickname = "test1", totalStudyTime = 0, userLevelId = 1, profileImage = "test1_profile_img",
            role = UserRole.USER, active = true, createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())

        Mockito.`when`(friendRelationRepository.save(any(FriendRelation::class.java))).thenReturn(Mono.just(savedFriendRelation))

        StepVerifier.create(friendRelationService.create(1L, 2L))
            .expectNext(FriendRelationDto.from(savedFriendRelation, followUser))
            .verifyComplete()
    }

    @Test
    fun `요청자 ID로 친구 관계 조회`() {
        val friendDetails = listOf(
            FriendDetailDto(followId = 2L, nickname = "test1", totalStudyTime = 100, profileImage = "test1_profile_img"),
            FriendDetailDto(followId = 3L, nickname = "test2", totalStudyTime = 200, profileImage = "test2_profile_img")
        )

        Mockito.`when`(friendRelationNativeSqlRepository.findFriendDetails(1L)).thenReturn(Flux.fromIterable(friendDetails))

        StepVerifier.create(friendRelationService.getFriendRelations(1L))
            .expectNext(friendDetails[0], friendDetails[1])
            .verifyComplete()
    }
}
