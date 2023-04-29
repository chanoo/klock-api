package app.klock.api.functional.timer

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class TimerFocusRouter(private val timerFocusHandler: TimerFocusHandler) {

  @Bean
  fun timerRoutes(): RouterFunction<ServerResponse> = coRouter {
    "/api/focus-timers".nest {
      POST("").invoke(timerFocusHandler::createFocusTimer)
      POST("/{id}").invoke(timerFocusHandler::updateFocusTimer)
      DELETE("/{id}").invoke(timerFocusHandler::deleteFocusTimer)
    }
  }
}
