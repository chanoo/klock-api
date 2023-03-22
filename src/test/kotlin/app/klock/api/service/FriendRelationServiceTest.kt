package app.klock.api.service

import app.klock.api.domain.entity.FriendRelation
import app.klock.api.repository.FriendRelationRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(MockitoExtension::class)
class FriendRelationServiceTest {

    @Mock
    private lateinit var friendRelationRepository: FriendRelationRepository

    private lateinit var friendRelationService: FriendRelationService

    @BeforeEach
    fun setUp() {
        friendRelationService = FriendRelationService(friendRelationRepository)
    }

    @Test
    fun `친구 관계 생성`() {
        val friendRelation = FriendRelation(requesterId = 1L, friendId = 2L)
        val savedFriendRelation = friendRelation.copy(id = 1L)

        Mockito.`when`(friendRelationRepository.save(any(FriendRelation::class.java))).thenReturn(Mono.just(savedFriendRelation))

        StepVerifier.create(friendRelationService.create(1L, 2L))
            .expectNext(savedFriendRelation)
            .verifyComplete()
    }

    @Test
    fun `요청자 ID로 친구 관계 조회`() {
        val friendRelations = listOf(
            FriendRelation(id = 1L, requesterId = 1L, friendId = 2L),
            FriendRelation(id = 2L, requesterId = 1L, friendId = 3L)
        )

        Mockito.`when`(friendRelationRepository.findByRequesterId(1L)).thenReturn(Flux.fromIterable(friendRelations))

        StepVerifier.create(friendRelationService.getFriendRelationsByRequesterId(1L))
            .expectNext(friendRelations[0], friendRelations[1])
            .verifyComplete()
    }
}
