package app.klock.api.functional.timer

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class TimerRouter(private val timerHandler: TimerHandler) {

  @Bean
  fun timerRoutes(): RouterFunction<ServerResponse> = coRouter {
    "/api/timers".nest {
      GET("").invoke(timerHandler::getAllTimersByUserId)
    }
  }
}
