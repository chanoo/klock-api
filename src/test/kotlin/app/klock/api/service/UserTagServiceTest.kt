package app.klock.api.service

import app.klock.api.domain.entity.UserTag
import app.klock.api.repository.UserTagRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(MockitoExtension::class)
class UserTagServiceTest {

  @Mock
  private lateinit var userTagRepository: UserTagRepository

  @InjectMocks
  private lateinit var userTagService: UserTagService

  private lateinit var userTags: List<UserTag>

  @BeforeEach
  fun setUp() {
    userTags = listOf(
      UserTag(id = 1L, userId = 1L, tagId = 1L),
      UserTag(id = 2L, userId = 1L, tagId = 2L),
      UserTag(id = 3L, userId = 2L, tagId = 1L)
    )
  }

  // findByAccountId 테스트
  @Test
  fun `findByUserId 테스트`() {
    val userId: Long = 1L

    // 리포지토리 모의
    Mockito.`when`(userTagRepository.findByUserId(userId)).thenReturn(Flux.fromIterable(userTags.filter { it.userId == userId }))

    // 서비스 메소드 호출 및 결과 확인
    val result = userTagService.findByUserId(userId)

    StepVerifier.create(result)
      .expectNext(userTags[0])
      .expectNext(userTags[1])
      .verifyComplete()

    // 리포지토리 메소드 호출 확인
    Mockito.verify(userTagRepository).findByUserId(userId)
  }

  // create 테스트
  @Test
  fun `create 테스트`() {
    val newUserTag = UserTag(userId = 3L, tagId = 2L)

    // 리포지토리 모의
    Mockito.`when`(userTagRepository.save(newUserTag)).thenReturn(Mono.just(newUserTag.copy(id = 4L)))

    // 서비스 메소드 호출 및 결과 확인
    val result = userTagService.create(newUserTag)

    StepVerifier.create(result)
      .expectNext(newUserTag.copy(id = 4L))
      .verifyComplete()

    // 리포지토리 메소드 호출 확인
    Mockito.verify(userTagRepository).save(newUserTag)
  }
}
