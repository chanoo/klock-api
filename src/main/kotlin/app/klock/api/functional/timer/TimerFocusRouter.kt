package app.klock.api.functional.timer

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class TimerFocusRouter(private val timerFocusHandler: TimerFocusHandler) {
  @Bean
  fun timerFocusRoutes() = router {
    "/api/v1/focus-timers".nest {
      POST("", timerFocusHandler::createFocusTimer)
      PUT("/{id}", timerFocusHandler::updateFocusTimer)
      DELETE("/{id}", timerFocusHandler::deleteFocusTimer)
    }
  }
}
