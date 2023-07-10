package app.klock.api.functional.studySession

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router


@Configuration
class StudySessionRouter(private val studySessionHandler: StudySessionHandler) {

    @Bean
    fun studySessionRoutes() = router {
        "/api/v1/study-sessions".nest {
            GET("", studySessionHandler::getStudySessionByUserIdAndDate)
            GET("/period", studySessionHandler::getStudySessionByUserIdAndPeriod)
            POST("", studySessionHandler::create)
            PUT("/{id}", studySessionHandler::update)
        }
    }

}
