package app.klock.api.functional.timer

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class TimerFocusRouter(private val timerFocusHandler: TimerFocusHandler) {
  @Bean
  fun timerFocusRoutes() = router {
    "/api/focus-timers".nest {
      POST("", timerFocusHandler::createFocusTimer)
      POST("/{id}", timerFocusHandler::updateFocusTimer)
      DELETE("/{id}", timerFocusHandler::deleteFocusTimer)
    }
  }
}
