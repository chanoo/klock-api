package app.klock.api.functional.account

import app.klock.api.domain.entity.Account
import app.klock.api.domain.entity.AccountRole
import app.klock.api.functional.auth.dto.*
import app.klock.api.service.AccountService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Component
class AccountHandler(private val accountService: AccountService) {
    private val logger = LoggerFactory.getLogger(AccountHandler::class.java)

    // 사용자 목록 조회
    fun getAllUsers(request: ServerRequest): Mono<ServerResponse> {
        val usersFlux = accountService.findAll() ?: Flux.empty<Account>()
        return usersFlux.map { user -> UserResponse(user.id, user.username, user.email) }
            .collectList()
            .flatMap { users -> ServerResponse.ok().body(BodyInserters.fromValue(users)) }
    }

    // 사용자 상세 조회
    fun getUserById(request: ServerRequest): Mono<ServerResponse> =
        accountService.findById(request.pathVariable("id").toLong())
            .flatMap { user ->
                ServerResponse.ok().bodyValue(UserResponse(user.id, user.username, user.email))
            }
            .switchIfEmpty(ServerResponse.notFound().build())

    // 사용자 수정
    fun updateUser(request: ServerRequest): Mono<ServerResponse> =
        request.bodyToMono(UpdateUserRequest::class.java)
            .flatMap { userRequest ->
                val userId = request.pathVariable("id").toLong()
                accountService.update(
                    id = userId,
                    user = Account(
                        id = userRequest.id,
                        username = userRequest.name,
                        email = userRequest.email,
                        hashedPassword = userRequest.password,
                        role = AccountRole.USER,
                        active = true,
                        totalStudyTime = 0,
                        accountLevelId = 1,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    )
                )
            }
            .flatMap { user ->
                ServerResponse.ok().bodyValue(UpdateUserResponse(user.id, user.username, user.email))
            }
            .switchIfEmpty(ServerResponse.notFound().build())

    // 사용자 삭제
    fun deleteUser(request: ServerRequest): Mono<ServerResponse> =
        accountService.deleteById(request.pathVariable("id").toLong())
            .then(ServerResponse.noContent().build())

    // 계정 비밀번호 변경
    fun changePassword(request: ServerRequest): Mono<ServerResponse> =
        request.bodyToMono(ChangePasswordRequest::class.java)
            .flatMap { changePasswordRequest ->
                val accountId = request.pathVariable("id").toLong()
                accountService.changePassword(accountId, changePasswordRequest.currentPassword, changePasswordRequest.newPassword)
            }
            .flatMap { account ->
                ServerResponse.ok().bodyValue(AccountResponse.from(account))
            }
            .switchIfEmpty(ServerResponse.notFound().build())

}
