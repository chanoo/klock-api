package app.klock.api.service

import app.klock.api.aws.s3.service.S3Service
import app.klock.api.domain.entity.UserTrace
import app.klock.api.functional.userTrace.CreateContentTrace
import app.klock.api.functional.userTrace.UserTraceDto
import app.klock.api.repository.UserRepository
import app.klock.api.repository.UserTraceNativeSqlRepository
import app.klock.api.repository.UserTraceRepository
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

@Service
class UserTraceService(
  private val userTraceRepository: UserTraceRepository,
  private val userTraceNativeSqlRepository: UserTraceNativeSqlRepository,
  private val userRepository: UserRepository,
  private val s3Service: S3Service,
  @Value("\${cloud.aws.s3.path-user-trace-image}") private val userTraceImagePath: String,
  @Value("\${cloud.aws.s3.endpoint}") private val s3Endpoint: String
) {

  fun createContentWithImage(
    userId: Long,
    userTrace: CreateContentTrace,
    imageBytes: ByteArray?,
    originFileName: String?
  ): Mono<UserTraceDto> {
    return if (imageBytes != null && originFileName != null) {
      // 이미지를 S3에 업로드하고, 업로드된 이미지 URL로 UserTrace 저장
      s3Service.uploadFile(userTraceImagePath, imageBytes, originFileName)
        .flatMap { imageUrl ->
          saveUserTrace(userId, userTrace, "$s3Endpoint/$imageUrl")
        }
    } else {
      // 이미지가 없는 경우, imageUrl을 null로 하여 UserTrace 저장
      saveUserTrace(userId, userTrace, null)
    }
  }

  private fun saveUserTrace(userId: Long, userTrace: CreateContentTrace, imageUrl: String?): Mono<UserTraceDto> {
    val traceToSave = UserTrace(
      userId = userId,
      writeUserId = userTrace.writeUserId,
      type = userTrace.type,
      contents = userTrace.contents,
      contentsImage = imageUrl,
      heartCount = 0
    )
    return userTraceRepository.save(traceToSave)
      .flatMap { savedTrace ->
        convertToUserTraceDto(savedTrace)
      }
  }

  fun updateHeart(traceId: Long, heartCount: Int): Mono<UserTraceDto> {
    return userTraceRepository.findById(traceId)
      .flatMap { trace ->
        userTraceRepository.save(
          trace.copy(
            heartCount = trace.heartCount + heartCount
          )
        )
      }
      .flatMap { trace ->
        convertToUserTraceDto(trace)
      }
      .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND, "Trace not found with id: $traceId")))
  }

  fun cancelHeart(traceId: Long): Mono<UserTraceDto> {
    return userTraceRepository.findById(traceId)
      .flatMap { trace ->
        if (trace.heartCount > 0) {
          userTraceRepository.save(
            trace.copy(
              heartCount = trace.heartCount -1
            )
          )
        } else {
            Mono.just(trace)
        }
      }
      .flatMap { trace ->
        convertToUserTraceDto(trace)
      }
      .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND, "Trace not found with id: $traceId")))
  }

  fun getTraces(userId: Long, page: Int, size: Int): Flux<UserTraceDto> {
    return userTraceNativeSqlRepository.getTraces(userId, page, size)
  }

//    fun createContent(userId: Long, userTrace: CreateContentTrace): Mono<UserTraceDto> {
//        return userTraceRepository.save(
//            UserTrace(
//                userId = userId,
//                writeUserId = userTrace.writeUserId,
//                contents = userTrace.contents)
//        )
//            .flatMap { trace ->
//                convertToUserTraceDto(trace)
//            }
//    }
//
//    fun createImage(userId: Long, writeUserId: Long, imageBytes: ByteArray, originFileName: String): Mono<UserTraceDto> {
//        return s3Service.uploadFile(userTraceImagePath, imageBytes, originFileName)
//            .flatMap { key ->
//                userTraceRepository.save(
//                    UserTrace(
//                        userId = userId,
//                        writeUserId = writeUserId,
//                        contentsImage = "$s3Endpoint/$key"
//                    )
//                )
//            }
//            .flatMap { trace ->
//                convertToUserTraceDto(trace)
//            }
//    }

  private fun convertToUserTraceDto(trace: UserTrace): Mono<UserTraceDto> {
    return userRepository.findById(trace.writeUserId)
      .map { user -> UserTraceDto.from(trace, user) }
  }

  fun deleteUserTrace(id: Long): Mono<Boolean> {
    return userTraceRepository.findById(id)
      .flatMap { userTrace ->
        userTrace.contentsImage?.let { s3Service.deleteFile(getS3Key(it)) } ?: Mono.just(true)
        userTraceRepository.deleteById(id).map { true }
      }
      .then(userTraceRepository.findById(id).hasElement().map { !it })
  }

  fun getS3Key(url: String): String {
    return url.replace("$s3Endpoint/", "")
  }
}
