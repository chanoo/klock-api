package app.klock.api.functional.timer

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class TimerExamRouter(private val timerExamHandler: TimerExamHandler) {
  @Bean
  fun timerExamRoutes() = router {
    "/api/v1/exam-timers".nest {
      POST("", timerExamHandler::createExamTimer)
      PUT("/{id}", timerExamHandler::updateExamTimer)
      DELETE("/{id}", timerExamHandler::deleteExamTimer)
    }
  }
}
