package app.klock.api.functional.timer

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class TimerRouter(private val timerHandler: TimerHandler) {

  @Bean
  fun timerRoutes(): RouterFunction<ServerResponse> = router {
    "/api/timers".nest {
      GET("", timerHandler::getAllTimersByUserId)
      PUT("", timerHandler::updateTimersSeq)
    }
  }
}
