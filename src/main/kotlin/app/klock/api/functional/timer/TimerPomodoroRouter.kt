package app.klock.api.functional.timer

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class TimerPomodoroRouter(private val timerPomodoroHandler: TimerPomodoroHandler) {

  @Bean
  fun timerPomodoroRoutes(): RouterFunction<ServerResponse> = coRouter {
    "/api/pomodoro-timers".nest {
      POST("").invoke(timerPomodoroHandler::createPomodoroTimer)
      POST("/{id}").invoke(timerPomodoroHandler::updatePomodoroTimer)
      DELETE("/{id}").invoke(timerPomodoroHandler::deletePomodoroTimer)
    }
  }
}
