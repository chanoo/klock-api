package app.klock.api.functional.user

import app.klock.api.domain.entity.User
import app.klock.api.domain.entity.UserRole
import app.klock.api.functional.auth.dto.ChangePasswordRequest
import app.klock.api.functional.auth.dto.UpdateUserRequest
import app.klock.api.functional.auth.dto.UpdateUserResponse
import app.klock.api.functional.auth.dto.UserResponse
import app.klock.api.service.UserService
import org.springframework.http.HttpStatus
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
    val usersFlux = userService.findAll()
    return usersFlux.map { user -> UserResponse(user.id, user.username, user.email) }
      .collectList()
      .flatMap { users -> ServerResponse.ok().body(BodyInserters.fromValue(users)) }
  }

  // 사용자 상세 조회
  fun getUserById(request: ServerRequest): Mono<ServerResponse> {
    val requestedUserId = request.pathVariable("id").toLong()

    return request.principal()
      .flatMap { principal ->
        if (principal.name != requestedUserId.toString()) {
          ServerResponse.status(HttpStatus.FORBIDDEN)
            .bodyValue(mapOf("error" to "You are not authorized to access this user information."))
        } else {
          userService.findById(requestedUserId)
            .flatMap { user ->
              ServerResponse.ok().bodyValue(UserResponse(user.id, user.username, user.email))
            }
            .switchIfEmpty(ServerResponse.notFound().build())
        }
      }
  }

  // 사용자 수정
  fun updateUser(request: ServerRequest): Mono<ServerResponse> =
    request.bodyToMono(UpdateUserRequest::class.java)
      .flatMap { userRequest ->
        val userId = request.pathVariable("id").toLong()
        userService.update(
          id = userId,
          user = User(
            id = userRequest.id,
            username = userRequest.name,
            email = userRequest.email,
            hashedPassword = userRequest.password,
            role = UserRole.USER,
            active = true,
            totalStudyTime = 0,
            userLevelId = 1,
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
        ServerResponse.ok().bodyValue(UserResponse.from(user))
      }
      .switchIfEmpty(ServerResponse.notFound().build())

}
