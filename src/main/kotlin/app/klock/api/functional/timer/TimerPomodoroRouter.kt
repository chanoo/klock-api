package app.klock.api.functional.timer

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.function.server.router

@Configuration
class TimerPomodoroRouter(private val timerPomodoroHandler: TimerPomodoroHandler) {
  
  @Bean
  fun timerPomodoroRoutes() = router {
    "/api/v1/pomodoro-timers".nest {
      POST("", timerPomodoroHandler::createPomodoroTimer)
      PUT("/{id}", timerPomodoroHandler::updatePomodoroTimer)
      DELETE("/{id}", timerPomodoroHandler::deletePomodoroTimer)
    }
  }
}
