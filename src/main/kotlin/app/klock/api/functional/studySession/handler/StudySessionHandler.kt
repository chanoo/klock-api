package app.klock.api.functional.studySession.handler

import app.klock.api.functional.studySession.dto.CreateStudySessionResponse
import app.klock.api.functional.studySession.dto.StudySessionByUserIdAndDateRequest
import app.klock.api.service.StudySessionService
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class StudySessionHandler(private val studySessionService: StudySessionService) {

    // userid 로 studySession 찾기
    fun getStudySessionByUserId(request: ServerRequest): Mono<ServerResponse> {
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

}
