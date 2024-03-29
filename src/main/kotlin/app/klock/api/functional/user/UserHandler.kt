package app.klock.api.functional.user

import app.klock.api.service.UserService
import app.klock.api.utils.JwtUtils
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class UserHandler(
  private val userService: UserService,
  private val jwtUtils: JwtUtils,
) {

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

  // 프로필 이미지 저장/변경
  fun updateProfileImage(request: ServerRequest): Mono<ServerResponse> =
    request.body(BodyExtractors.toMultipartData())
      .flatMap { parts ->
        val userId = request.pathVariable("id").toLong()
        val imagePart = parts.toSingleValueMap()["file"] as FilePart

        val imageBytesMono = DataBufferUtils.join(imagePart.content()).flatMap { dataBuffer ->
          val bytes = ByteArray(dataBuffer.readableByteCount())
          dataBuffer.read(bytes)
          DataBufferUtils.release(dataBuffer)
          Mono.just(bytes)
        }

        // Process the image asynchronously
        imageBytesMono.flatMap { imageBytes ->
          userService.updateProfileImage(userId, imageBytes, imagePart.filename())
            .flatMap { user ->
              ServerResponse.ok().bodyValue(UserInfoDto.from(user))
            }
        }
      }.onErrorResume { e ->
        ServerResponse.badRequest().bodyValue(mapOf("error" to (e.message ?: "Unknown error")))
      }


  // 닉네임으로 사용자 검색
  fun searchByNickname(request: ServerRequest): Mono<ServerResponse> =
    jwtUtils.getUserIdFromToken()
      .flatMap { userId ->
        request.bodyToMono(UserSearchRequest::class.java)
          .flatMap { searchByNicknameRequest ->
            userService.searchByNickname(userId, searchByNicknameRequest.nickname)
              .flatMap { user ->
                ServerResponse.ok().bodyValue(SimpleUserInfoDto.from(user))
              }
              .switchIfEmpty(ServerResponse.noContent().build())
          }
      }
      .onErrorResume { e ->
        ServerResponse.badRequest().bodyValue(mapOf("error" to (e.message ?: "Unknown error")))
      }
}
