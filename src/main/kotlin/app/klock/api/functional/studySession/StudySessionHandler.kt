package app.klock.api.functional.studySession

import app.klock.api.domain.entity.StudySession
import app.klock.api.service.StudySessionService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.time.LocalDate

@Component
class StudySessionHandler(private val studySessionService: StudySessionService) {

  // userid 로 studySession 찾기
  fun getStudySessionByUserIdAndDate(request: ServerRequest): Mono<ServerResponse> {
    val userId = request.queryParam("userId").orElse(null)?.toLongOrNull()
    val date = request.queryParam("date").orElse(null)?.let { LocalDate.parse(it) }

    return if (userId != null && date != null) {
      studySessionService.findByUserIdAndStartTimeBetween(userId, date)
        .map { studySession ->
          StudySessionDto.from(studySession)
        }
        .collectList()
        .flatMap { studySessions -> ServerResponse.ok().body(BodyInserters.fromValue(studySessions)) }
    } else {
      ServerResponse.badRequest()
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(mapOf("error" to "Invalid query parameters"))
    }
  }

  fun getStudySessionByUserIdAndPeriod(request: ServerRequest): Mono<ServerResponse> {
    val userId = request.queryParam("userId").orElse(null)?.toLongOrNull()
    val startDate = request.queryParam("startDate").orElse(null)?.let { LocalDate.parse(it) }
    val endDate = request.queryParam("endDate").orElse(null)?.let { LocalDate.parse(it) }

    return if (userId != null && startDate != null && endDate != null && !startDate.isAfter(endDate)) {
      studySessionService.findByUserIdAndStartTimeBetween(userId, startDate, endDate)
        .map { studySession ->
          StudySessionDto.from(studySession)
        }
        .collectList()
        .flatMap { studySessions -> ServerResponse.ok().body(BodyInserters.fromValue(studySessions)) }
    } else {
      ServerResponse.badRequest()
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(mapOf("error" to "Invalid query parameters"))
    }
  }

  fun create(request: ServerRequest): Mono<ServerResponse> {
    return request.bodyToMono(StudySessionDto::class.java)
      .flatMap { studySessionDto ->
        studySessionService.create(studySessionDto.toDomain())
          .flatMap { createdSession ->
            ServerResponse.status(HttpStatus.CREATED).body(BodyInserters.fromValue(createdSession))
          }
      }
  }

  fun update(request: ServerRequest): Mono<ServerResponse> {
    val id = request.pathVariable("id").toLong()

    return request.bodyToMono(StudySessionDto::class.java)
      .flatMap { studySessionDto ->
        studySessionService.update(id, studySessionDto.toDomain())
          .flatMap { updatedSession ->
            ServerResponse.ok().body(BodyInserters.fromValue(updatedSession))
          }
      }
      .onErrorResume { e ->
        ServerResponse.badRequest().bodyValue(mapOf("error" to (e.message ?: "Unknown error"))) }
  }
}
