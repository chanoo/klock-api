package app.klock.api.functional.userTrace

import app.klock.api.domain.entity.UserTraceType
import app.klock.api.service.UserTraceHeartService
import app.klock.api.service.UserTraceService
import app.klock.api.utils.JwtUtils
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.HttpStatus
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.Part
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets

private val logger = KotlinLogging.logger {}

@Component
class UserTraceHandler(
  private val userTraceService: UserTraceService,
  private val userTraceHeartService: UserTraceHeartService,
  val jwtUtils: JwtUtils,
  val objectMapper: ObjectMapper
) {

  // 담벼락 리스트
  fun getUserTrace(request: ServerRequest): Mono<ServerResponse> {
    return jwtUtils.getUserIdFromToken()
      .flatMap { userId ->
        val page = request.queryParam("page").orElse("0").toInt()
        val size = request.queryParam("size").orElse("10").toInt()

        userTraceService.getTraces(userId, page, size)
          .collectList()
          .flatMap { traces ->
            ServerResponse.ok().bodyValue(traces)
          }
      }.onErrorResume { e ->
        ServerResponse.badRequest().bodyValue(mapOf("error" to (e.message ?: "Unknown error")))
      }
  }

  fun createContentWithImage(request: ServerRequest): Mono<ServerResponse> {
    return jwtUtils.getUserIdFromToken()
      .flatMap { userId ->
        request.body(BodyExtractors.toMultipartData())
          .flatMap { parts ->
            val partMap = parts.toSingleValueMap()

            // 텍스트 데이터 처리
            val contentTracePart = partMap["contentTrace"] as? Part
            val contentTraceMono = contentTracePart?.let { part ->
              DataBufferUtils.join(part.content())
                .flatMap { dataBuffer ->
                  val bytes = ByteArray(dataBuffer.readableByteCount())
                  dataBuffer.read(bytes)
                  DataBufferUtils.release(dataBuffer)
                  Mono.just(String(bytes, StandardCharsets.UTF_8))
                }
                .map { jsonString ->
                  // JSON 문자열을 CreateContentTrace 객체로 변환
                  val contentTrace = objectMapper.readValue(jsonString, CreateContentTrace::class.java)
                  // Type 값 체크
                  if (contentTrace.type != UserTraceType.ACTIVITY && contentTrace.type != UserTraceType.STUDY_START && contentTrace.type != UserTraceType.STUDY_END) {
                    throw IllegalArgumentException("Invalid type value. Must be either 'ACTIVITY' or 'STUDY_START' or 'STUDY_END'")
                  }
                  contentTrace
                }
            } ?: Mono.error(IllegalArgumentException("Content trace is missing."))

            contentTraceMono.flatMap { contentTrace ->
              val imagePart = partMap["image"] as? FilePart

              if (imagePart != null) {
                DataBufferUtils.join(imagePart.content()).flatMap { dataBuffer ->
                  val bytes = ByteArray(dataBuffer.readableByteCount())
                  dataBuffer.read(bytes)
                  DataBufferUtils.release(dataBuffer)
                  userTraceService.createContentWithImage(userId, contentTrace, bytes, imagePart.filename())
                }
              } else {
                userTraceService.createContentWithImage(userId, contentTrace, null, null)
              }
            }.flatMap { userTrace ->
              ServerResponse.status(HttpStatus.CREATED).bodyValue(userTrace)
            }
          }
      }.onErrorResume { e ->
        ServerResponse.badRequest().bodyValue(mapOf("error" to (e.message ?: "Unknown error")))
      }
  }

  // 담벼락 좋아요
  fun updateHeart(request: ServerRequest): Mono<ServerResponse> {
    val traceId = request.pathVariable("trace_id").toLong()
    return jwtUtils.getUserIdFromToken()
      .flatMap { userId ->
        userTraceHeartService.updateHeart(traceId, userId)
          .flatMap { userTraceHeart ->
            userTraceService.updateHeart(traceId, 1)
              .flatMap { userTrace ->
                ServerResponse.ok().bodyValue(userTrace)
              }
              .onErrorResume { e ->
                if (e is ResponseStatusException && e.statusCode == HttpStatus.NOT_FOUND) {
                  ServerResponse.notFound().build()
                } else {
                  ServerResponse.badRequest().bodyValue(mapOf("error" to (e.message ?: "Unknown error")))
                }
              }
          }
      }.onErrorResume { e ->
        ServerResponse.badRequest().bodyValue(mapOf("error" to (e.message ?: "Unknown error")))
      }
  }

  fun cancelHeart(request: ServerRequest): Mono<ServerResponse> {
    val traceId = request.pathVariable("trace_id").toLong()
    return jwtUtils.getUserIdFromToken()
      .flatMap { userId ->
        userTraceHeartService.cancelHeart(traceId, userId)
          .flatMap { userTraceHeart ->
            userTraceService.cancelHeart(traceId)
              .flatMap { userTrace ->
                ServerResponse.ok().bodyValue(userTrace)
              }
              .onErrorResume { e ->
                if (e is ResponseStatusException && e.statusCode == HttpStatus.NOT_FOUND) {
                  ServerResponse.notFound().build()
                } else {
                  ServerResponse.badRequest().bodyValue(mapOf("error" to (e.message ?: "Unknown error")))
                }
              }
          }
      }.onErrorResume { e ->
        ServerResponse.badRequest().bodyValue(mapOf("error" to (e.message ?: "Unknown error")))
      }
  }

  // 담벼락 삭제
  fun deleteUserTrace(request: ServerRequest): Mono<ServerResponse> {
    val traceId = request.pathVariable("trace_id").toLong()
    return userTraceService.deleteUserTrace(traceId).flatMap { isDeleted ->
      if (isDeleted) {
        ServerResponse.status(HttpStatus.NO_CONTENT).build()
      } else {
        ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("User Trace not found")
      }
    }
  }

  // 담벼락 컨텐츠 생성
//  fun createContent(request: ServerRequest): Mono<ServerResponse> =
//    jwtUtils.getUserIdFromToken()
//      .flatMap { userId ->
//        request.bodyToMono(CreateContentTrace::class.java)
//          .flatMap {
//              content -> userTraceService.createContent(userId, content)
//          }
//          .flatMap { userTrace ->
//            ServerResponse.status(HttpStatus.CREATED).bodyValue(userTrace)
//          }
//      }.onErrorResume { e ->
//        ServerResponse.badRequest().bodyValue(mapOf("error" to (e.message ?: "Unknown error")))
//      }

  // 담벼락 이미지 생성
//  fun createImage(request: ServerRequest): Mono<ServerResponse> =
//    jwtUtils.getUserIdFromToken()
//      .flatMap { userId ->
//        request.body(BodyExtractors.toMultipartData())
//          .flatMap { parts ->
//            val writeUserId = request.pathVariable("write_user_id").toLong()
//            val imagePart = parts.toSingleValueMap()["file"] as FilePart
//
//            val imageBytesMono = DataBufferUtils.join(imagePart.content()).flatMap { dataBuffer ->
//              val bytes = ByteArray(dataBuffer.readableByteCount())
//              dataBuffer.read(bytes)
//              DataBufferUtils.release(dataBuffer)
//              Mono.just(bytes)
//            }
//
//            imageBytesMono.flatMap { imageBytes ->
//              userTraceService.createImage(userId, writeUserId, imageBytes, imagePart.filename())
//                .flatMap { userTrace ->
//                  ServerResponse.status(HttpStatus.CREATED).bodyValue(userTrace)
//                }
//            }
//          }.onErrorResume { e ->
//            ServerResponse.badRequest().bodyValue(mapOf("error" to (e.message ?: "Unknown error")))
//          }
//      }
}
