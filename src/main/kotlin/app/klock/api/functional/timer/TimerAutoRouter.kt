package app.klock.api.functional.timer

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class TimerAutoRouter(private val timerAutoHandler: TimerAutoHandler) {
  @Bean
  fun timerAutoRoutes() = router {
    "/api/v1/auto-timers".nest {
      POST("", timerAutoHandler::createAutoTimer)
      PUT("/{id}", timerAutoHandler::updateAutoTimer)
      DELETE("/{id}", timerAutoHandler::deleteAutoTimer)
    }
  }
}
