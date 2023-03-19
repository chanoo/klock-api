package app.klock.api.functional.accountTag

import app.klock.api.domain.entity.AccountTag
import app.klock.api.service.AccountTagService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.util.*

@Component
class AccountTagHandler(private val accountTagService: AccountTagService) {
    // accountId를 사용하여 태그 가져오기
    fun getAccountTags(request: ServerRequest): Mono<ServerResponse> {
        val accountId = request.queryParam("accountId").flatMap { it.toLongOrNull()?.let { id -> Optional.of(id) } }.orElse(null)
        return if (accountId != null) {
            accountTagService.findByAccountId(accountId)
                .collectList()
                .flatMap { tags ->
                    ServerResponse.ok().bodyValue(tags)
                }
        } else {
            ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("Invalid accountId")
        }
    }

    fun createAccountTag(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(AccountTag::class.java)
            .flatMap { accountTag -> accountTagService.create(accountTag) }
            .flatMap { createdAccountTag -> ServerResponse.status(HttpStatus.CREATED).bodyValue(createdAccountTag) }
    }
}
