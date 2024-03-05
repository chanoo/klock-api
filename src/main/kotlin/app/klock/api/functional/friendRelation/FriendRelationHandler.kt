package app.klock.api.functional.friendRelation

import app.klock.api.service.FriendRelationService
import app.klock.api.utils.CryptoUtils
import app.klock.api.utils.JwtUtils
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.net.URI
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class FriendRelationHandler(
  private val friendRelationService: FriendRelationService,
  private val jwtUtils: JwtUtils,
  private val cryptoUtils: CryptoUtils
) {

  /**
   * 팔로우 요청
   */
  fun follow(request: ServerRequest): Mono<ServerResponse> {
    return jwtUtils.getUserIdFromToken()
      .flatMap { userId ->
        request.bodyToMono(FriendRelationRequest::class.java)
          .flatMap { friendRelationRequest ->
            validateFriendRelation(userId, friendRelationRequest.followId)
              .flatMap { _ ->
                friendRelationService.create(
                  userId = userId,
                  followId = friendRelationRequest.followId
                )
              }
              .flatMap { friendRelation ->
                ServerResponse.created(URI.create("/api/v1/friend-relations/${friendRelation.id}"))
                  .bodyValue(friendRelation)
              }
              .switchIfEmpty(ServerResponse.status(HttpStatus.BAD_REQUEST).build())
          }
      }
  }

  /**
   * 유저가 팔로우한 유저를 다시 언팔로우
   * 팔로우한 유저를 찾지 못하면 404 에러 발생
   */
  fun unfollow(request: ServerRequest): Mono<ServerResponse> {
    return jwtUtils.getUserIdFromToken()
      .flatMap { userId ->
        request.bodyToMono(FriendRelationRequest::class.java)
          .flatMap { friendRelationRequest ->
            friendRelationService.unfollow(
              userId = userId,
              followId = friendRelationRequest.followId
            )
          }
          .then(ServerResponse.noContent().build())
          .switchIfEmpty(ServerResponse.notFound().build())
      }
  }

  fun getFriendRelations(request: ServerRequest): Mono<ServerResponse> {
    return jwtUtils.getUserIdFromToken()
      .flatMap { userId ->
        friendRelationService.getFriendRelations(userId)
          .collectList()
          .flatMap { friendRelations ->
            ServerResponse.ok().bodyValue(friendRelations)
          }
      }
  }

  fun followFromQrCode(request: ServerRequest): Mono<ServerResponse> {
    return jwtUtils.getUserIdFromToken()
      .flatMap { userId ->
        request.bodyToMono(FollowFromQrCodeRequest::class.java)
          .flatMap { request ->
            val objectMapper = jacksonObjectMapper()
            val decryptData = cryptoUtils.decryptData(request.encryptedKey, request.followData)
            val followQrCodeData = objectMapper.readValue(decryptData, FollowQrCodeData::class.java)

            validateQrCodeExpireDate(followQrCodeData.expireDate)
              .flatMap {
                friendRelationService.followFromQrCode(userId, followQrCodeData.followId)
                  .flatMap { friendRelation ->
                    ServerResponse.created(URI.create("/api/v1/friend-relations/${friendRelation.id}"))
                      .bodyValue(friendRelation)
                  }
                  .switchIfEmpty(ServerResponse.status(HttpStatus.BAD_REQUEST).build())
              }.onErrorResume { e ->
                ServerResponse.badRequest().bodyValue(mapOf("error" to (e.message ?: "Unknown error")))
              }
          }
      }
  }

  fun validateQrCodeExpireDate(expireDateTimeString: String) : Mono<Boolean> {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val expireDateTime = LocalDateTime.parse(expireDateTimeString, formatter)
    if (expireDateTime.isBefore(LocalDateTime.now())) {
      return Mono.error(IllegalArgumentException("QR코드 유효기간이 만료되었습니다."))
    }
    return Mono.just(true)
  }

  /**
   * 친구 추가시 체크 사항
   * 1. 이미 추가된 친구일 경우
   * 2. 내 자신을 추가 하려는 경우
   */
    fun validateFriendRelation(userId: Long, followId: Long): Mono<Boolean> {
      if (userId == followId) {
        return Mono.error(IllegalArgumentException("자신을 친구로 추가할 수 없습니다."))
      }

      return friendRelationService.getFriendRelation(userId, followId)
        .handle<Boolean> { _, sink ->
          sink.error(IllegalArgumentException("이미 친구로 추가된 사용자입니다."))
        }
        .switchIfEmpty(Mono.just(true))
    }
}
