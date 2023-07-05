package app.klock.api.service

import app.klock.api.domain.entity.FriendRelation
import app.klock.api.functional.friemdRelation.FriendRelationDto
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

@ExtendWith(MockitoExtension::class)
@SpringBootTest(classes = [FriendRelationService::class])
class FriendRelationServiceTest @Autowired constructor(
    private val friendRelationService: FriendRelationService
) {

    @MockBean
    private lateinit var friendRelationRepository: FriendRelationRepository

    @BeforeEach
    fun setUp() {
        Mockito.`when`(friendRelationRepository.findByUserIdAndFollowId(anyLong(), anyLong()))
            .thenReturn(Mono.empty())
    }

    @Test
    fun `팔로우 요청`() {
        val friendRelation = FriendRelation(userId = 1L, followId = 2L)
        val savedFriendRelation = friendRelation.copy(id = 1L)

        Mockito.`when`(friendRelationRepository.save(any(FriendRelation::class.java))).thenReturn(Mono.just(savedFriendRelation))

        StepVerifier.create(friendRelationService.create(1L, 2L))
            .expectNext(FriendRelationDto.from(savedFriendRelation))
            .verifyComplete()
    }

    @Test
    fun `요청자 ID로 친구 관계 조회`() {
        val friendRelations = listOf(
            FriendRelation(id = 1L, userId = 1L, followId = 2L, followed = true),
            FriendRelation(id = 2L, userId = 1L, followId = 3L, followed = true)
        )

        Mockito.`when`(friendRelationRepository.findByUserIdAndFollowed(1L, true)).thenReturn(Flux.fromIterable(friendRelations))

        StepVerifier.create(friendRelationService.getFriendRelations(1L))
            .expectNext(FriendRelationDto.from(friendRelations[0]), FriendRelationDto.from(friendRelations[1]))
            .verifyComplete()
    }
}
