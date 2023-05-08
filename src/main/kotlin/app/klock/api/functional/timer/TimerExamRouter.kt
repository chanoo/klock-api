package app.klock.api.functional.timer

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class TimerExamRouter(private val timerExamHandler: TimerExamHandler) {

  @Bean
  fun timerExamRoutes(): RouterFunction<ServerResponse> = coRouter {
    "/api/exam-timers".nest {
      POST("").invoke(timerExamHandler::createExamTimer)
      PUT("/{id}").invoke(timerExamHandler::updateExamTimer)
      DELETE("/{id}").invoke(timerExamHandler::deleteExamTimer)
    }
  }
}
