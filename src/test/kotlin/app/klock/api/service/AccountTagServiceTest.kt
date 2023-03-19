package app.klock.api.service

import app.klock.api.domain.entity.AccountTag
import app.klock.api.repository.AccountTagRepository
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
class AccountTagServiceTest {

    @Mock
    private lateinit var accountTagRepository: AccountTagRepository

    @InjectMocks
    private lateinit var accountTagService: AccountTagService

    private lateinit var accountTags: List<AccountTag>

    @BeforeEach
    fun setUp() {
        accountTags = listOf(
            AccountTag(id = 1L, accountId = 1L, tagId = 1L),
            AccountTag(id = 2L, accountId = 1L, tagId = 2L),
            AccountTag(id = 3L, accountId = 2L, tagId = 1L)
        )
    }

    // findByAccountId 테스트
    @Test
    fun `findByAccountId 테스트`() {
        val accountId: Long = 1L

        // 리포지토리 모의
        Mockito.`when`(accountTagRepository.findByAccountId(accountId)).thenReturn(Flux.fromIterable(accountTags.filter { it.accountId == accountId }))

        // 서비스 메소드 호출 및 결과 확인
        val result = accountTagService.findByAccountId(accountId)

        StepVerifier.create(result)
            .expectNext(accountTags[0])
            .expectNext(accountTags[1])
            .verifyComplete()

        // 리포지토리 메소드 호출 확인
        Mockito.verify(accountTagRepository).findByAccountId(accountId)
    }

    // create 테스트
    @Test
    fun `create 테스트`() {
        val newAccountTag = AccountTag(accountId = 3L, tagId = 2L)

        // 리포지토리 모의
        Mockito.`when`(accountTagRepository.save(newAccountTag)).thenReturn(Mono.just(newAccountTag.copy(id = 4L)))

        // 서비스 메소드 호출 및 결과 확인
        val result = accountTagService.create(newAccountTag)

        StepVerifier.create(result)
            .expectNext(newAccountTag.copy(id = 4L))
            .verifyComplete()

        // 리포지토리 메소드 호출 확인
        Mockito.verify(accountTagRepository).save(newAccountTag)
    }
}
