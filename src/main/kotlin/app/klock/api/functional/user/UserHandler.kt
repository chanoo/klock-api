package app.klock.api.functional.user

import app.klock.api.domain.entity.User
import app.klock.api.domain.entity.UserRole
import app.klock.api.service.UserService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Component
class UserHandler(private val userService: UserService) {

  // 사용자 목록 조회
  fun getAllUsers(request: ServerRequest): Mono<ServerResponse> {
    return userService.findAll()
      .collectList()
      .flatMap { users -> ServerResponse.ok().body(BodyInserters.fromValue(users)) }
  }

  // 사용자 상세 조회
  fun getUserById(request: ServerRequest): Mono<ServerResponse> {
    val requestedUserId = request.pathVariable("id").toLong()
    return userService.findById(requestedUserId)
      .flatMap { userInfo ->
        ServerResponse.ok().bodyValue(userInfo)
      }
      .switchIfEmpty(ServerResponse.notFound().build())
  }

  // 사용자 수정
  fun updateUser(request: ServerRequest): Mono<ServerResponse> =
    request.bodyToMono(UpdateUserRequest::class.java)
      .flatMap { userRequest ->
        val userId = request.pathVariable("id").toLong()
        userService.update(id = userId, updateUserRequest = userRequest)
      }
      .flatMap { userInfo ->
        ServerResponse.ok().bodyValue(userInfo)
      }
      .switchIfEmpty(ServerResponse.notFound().build())

  // 사용자 삭제
  fun deleteUser(request: ServerRequest): Mono<ServerResponse> =
    userService.deleteById(request.pathVariable("id").toLong())
      .then(ServerResponse.noContent().build())

  // 계정 비밀번호 변경
  fun changePassword(request: ServerRequest): Mono<ServerResponse> =
    request.bodyToMono(ChangePasswordRequest::class.java)
      .flatMap { changePasswordRequest ->
        val userId = request.pathVariable("id").toLong()
        userService.changePassword(userId, changePasswordRequest.currentPassword, changePasswordRequest.newPassword)
      }
      .flatMap { user ->
        ServerResponse.ok().bodyValue(UserInfoDto.from(user))
      }
      .switchIfEmpty(ServerResponse.notFound().build())

  // 닉네임 존재 여부 체크
  fun existedNickname(request: ServerRequest): Mono<ServerResponse> =
    request.bodyToMono(CheckNicknameRequest::class.java)
      .flatMap { checkNicknameRequest ->
        userService.existedNickname(checkNicknameRequest.nickname)
          .flatMap { exists ->
            ServerResponse.ok().bodyValue(mapOf("exists" to exists))
          }
      }.onErrorResume { e ->
        ServerResponse.badRequest().bodyValue(mapOf("error" to (e.message ?: "Unknown error")))
      }
}
