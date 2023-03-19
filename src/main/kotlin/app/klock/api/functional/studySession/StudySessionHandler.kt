package app.klock.api.functional.studySession

import app.klock.api.domain.entity.StudySession
import app.klock.api.functional.studySession.dto.CreateStudySessionResponse
import app.klock.api.functional.studySession.dto.StudySessionByUserIdAndDateRequest
import app.klock.api.service.StudySessionService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class StudySessionHandler(private val studySessionService: StudySessionService) {

    // userid 로 studySession 찾기
    fun getStudySessionByUserIdAndDate(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(StudySessionByUserIdAndDateRequest::class.java)
            .flatMap { request ->
                studySessionService.findByAccountIdAndStartTimeBetween(request.userId, request.date)
                    .map { studySession -> CreateStudySessionResponse(
                        startTime = studySession.startTime,
                        endTime = studySession.endTime,
                        userId = studySession.accountId)
                    }
                    .collectList()
                    .flatMap { studySessions -> ServerResponse.ok().body(BodyInserters.fromValue(studySessions)) }
            }
            .onErrorResume { error ->
                ServerResponse.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(mapOf("error" to error.message))
            }
    }


    fun create(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(StudySession::class.java)
            .flatMap { studySession ->
                studySessionService.create(studySession)
                    .flatMap { createdSession ->
                        ServerResponse.status(HttpStatus.CREATED).body(BodyInserters.fromValue(createdSession))
                    }
            }
    }

    fun update(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id").toLong()

        return request.bodyToMono(StudySession::class.java)
            .flatMap { studySession ->
                studySessionService.update(id, studySession)
                    .flatMap { updatedSession ->
                        ServerResponse.ok().body(BodyInserters.fromValue(updatedSession))
                    }
            }
    }

}
