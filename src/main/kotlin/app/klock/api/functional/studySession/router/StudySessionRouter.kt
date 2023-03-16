package app.klock.api.functional.studySession.router

import app.klock.api.functional.studySession.handler.StudySessionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class StudySessionRouter(private val studySessionHandler: StudySessionHandler) {

    @Bean
    fun studySessionRoutes() = router {
        "/api/study-sessions".nest {
            GET("", studySessionHandler::getStudySessionByUserId)
        }
    }

}
